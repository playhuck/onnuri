package com.side.onnuri.accounting.application.service;

import com.side.onnuri.accounting.application.dto.CompanyRulesDto;
import com.side.onnuri.accounting.domain.event.TransactionProcessedEvent;
import com.side.onnuri.accounting.domain.model.AccountCategory;
import com.side.onnuri.accounting.domain.model.BankTransaction;
import com.side.onnuri.accounting.domain.model.Company;
import com.side.onnuri.accounting.domain.model.TransactionClassification;
import com.side.onnuri.accounting.application.service.matching.AdvancedTransactionMatcher;
import com.side.onnuri.accounting.application.service.matching.MatchingResult;
import com.side.onnuri.accounting.application.service.matching.MatchingRule;
import com.side.onnuri.accounting.infrastructure.mapper.BankTransactionMapper;
import com.side.onnuri.accounting.infrastructure.persistence.*;
import com.side.onnuri.accounting.infrastructure.repository.*;
import com.side.onnuri.common.exception.EntityNotFoundException;
import com.side.onnuri.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionClassificationServiceImpl {
    
    private final BankTransactionRepository bankTransactionRepository;
    private final CompanyRepository companyRepository;
    private final AccountCategoryRepository accountCategoryRepository;
    private final TransactionClassificationRepository classificationRepository;
    private final TransactionEventRepository eventRepository;
    private final BankTransactionMapper transactionMapper;
    private final RuleVersionService ruleVersionService;
    private final ObjectMapper objectMapper;
    private final AdvancedTransactionMatcher advancedTransactionMatcher;
    
    @Transactional
    public void processTransactions(List<BankTransaction> transactions, CompanyRulesDto rules) {
        // 1. 업로드된 규칙을 Rule Version으로 저장
        RuleVersionEntity ruleVersion = saveRulesAsVersion(rules);
        
        // 2. 규칙 데이터를 DB에 저장하고 저장된 회사 목록 반환 (키워드 정보 포함)
        List<CompanyEntity> companies = saveRulesToDatabaseAndReturn(rules, ruleVersion.getId());
        
        // 4. 각 거래를 처리 (규칙 버전 ID 포함)
        for (BankTransaction transaction : transactions) {
            processTransaction(transaction, companies, ruleVersion.getId());
        }
        
        // 6. 규칙을 사용된 것으로 마킹 및 비활성화
        ruleVersionService.markRuleAsUsedAndDeactivate(ruleVersion.getId());
        
        log.info("총 {}건의 거래 처리 완료 (규칙 버전: {} - 비활성화됨)", transactions.size(), ruleVersion.getVersionName());
    }
    
    @Transactional
    public void processTransaction(BankTransaction transaction, List<CompanyEntity> companies, UUID ruleVersionId) {
        // 1. 거래 저장
        BankTransactionEntity savedTransaction = bankTransactionRepository.save(
                transactionMapper.toEntity(transaction));
        
        // 2. 분류 시도
        Optional<ClassificationResult> classificationResult = classifyTransaction(
                savedTransaction, companies, ruleVersionId);
        
        // 3. 분류 결과 저장
        TransactionClassification classification;
        TransactionProcessedEvent event;
        
        if (classificationResult.isPresent()) {
            ClassificationResult result = classificationResult.get();
            classification = TransactionClassification.createClassified(
                    savedTransaction.getId(),
                    mapToCompanyDomain(result.company),
                    mapToCategoryDomain(result.category, result.matchedKeyword),
                    result.matchedKeyword
            );
            event = TransactionProcessedEvent.createClassifiedEvent(
                    savedTransaction.getId(),
                    result.company.getId(),
                    result.category.getId(),
                    result.matchedKeyword
            );
        } else {
            classification = TransactionClassification.createUnclassified(savedTransaction.getId());
            event = TransactionProcessedEvent.createUnclassifiedEvent(savedTransaction.getId());
        }
        
        // 4. 분류 결과 및 이벤트 저장 (규칙 버전 ID 포함)
        saveClassificationAndEvent(classification, event, ruleVersionId);
    }
    
    private Optional<ClassificationResult> classifyTransaction(
            BankTransactionEntity transaction, List<CompanyEntity> companies, UUID ruleVersionId) {
        
        // 동적 구조에서 적요 추출 (다양한 헤더명 지원)
        String description = extractDescription(transaction);
        if (description == null || description.trim().isEmpty()) {
            log.warn("거래 적요를 찾을 수 없습니다: {}", transaction.getId());
            return Optional.empty();
        }
        
        // 동적 구조에서 금액 추출
        BigDecimal amount = extractAmount(transaction);
        
        // CompanyEntity를 MatchingRule로 변환 (현재 ruleVersionId 기준)
        List<MatchingRule> matchingRules = convertToMatchingRules(companies, ruleVersionId);
        
        // Trie 구성
        advancedTransactionMatcher.buildTrie(matchingRules);
        
        // 고급 매칭 엔진으로 최적 매칭 결과 찾기
        Optional<MatchingResult> matchingResult = advancedTransactionMatcher.findBestMatch(
                description, amount, matchingRules);
        
        if (matchingResult.isPresent()) {
            MatchingResult result = matchingResult.get();
            
            // MatchingResult를 ClassificationResult로 변환
            CompanyEntity matchedCompany = findCompanyById(companies, result.getCompanyId());
            AccountCategoryEntity matchedCategory = findCategoryById(companies, result.getCategoryId());
            
            if (matchedCompany != null && matchedCategory != null) {
                String matchedKeyword = result.getMatchedKeywords().isEmpty() ? 
                        "" : String.join(", ", result.getMatchedKeywords());
                        
                log.debug("고급 매칭 성공 - 거래: {}, 키워드: [{}], 회사: {}, 카테고리: {}, 점수: {}", 
                        description, matchedKeyword, matchedCompany.getCompanyName(), 
                        matchedCategory.getCategoryName(), result.getMatchingScore());
                        
                return Optional.of(new ClassificationResult(matchedCompany, matchedCategory, matchedKeyword));
            }
        }
        
        log.debug("매칭 실패 - 거래: {}", description);
        return Optional.empty();
    }
    
    private String extractDescription(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        
        // 다양한 헤더명으로 적요 찾기
        String[] possibleDescriptionKeys = {"적요", "description", "memo", "거래내용", "내용"};
        
        for (String key : possibleDescriptionKeys) {
            Object value = attributes.get(key);
            if (value instanceof String && !((String) value).trim().isEmpty()) {
                return (String) value;
            }
        }
        
        // 적요를 찾지 못한 경우, attributes를 순회하며 String 타입 중 가장 긴 것을 반환
        return attributes.values().stream()
                .filter(o -> o instanceof String)
                .map(o -> (String) o)
                .filter(value -> !value.trim().isEmpty())
                .max(Comparator.comparingInt(String::length))
                .orElse(null);
    }
    
    private BigDecimal extractAmount(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        
        // 다양한 헤더명으로 금액 찾기
        String[] possibleAmountKeys = {"거래금액", "금액", "amount", "value", "출금액", "입금액"};
        
        for (String key : possibleAmountKeys) {
            Object value = attributes.get(key);
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            } else if (value instanceof String) {
                try {
                    String strValue = ((String) value).replaceAll("[^0-9.-]", "");
                    if (!strValue.isEmpty()) {
                        return new BigDecimal(strValue);
                    }
                } catch (NumberFormatException e) {
                    // 무시하고 다음 키 시도
                }
            }
        }
        
        return null;
    }
    
    private List<MatchingRule> convertToMatchingRules(List<CompanyEntity> companies, UUID ruleVersionId) {
        List<MatchingRule> rules = new ArrayList<>();
        Set<String> ruleKeys = new HashSet<>();  // 중복 검사를 위한 Set
        
        for (CompanyEntity company : companies) {
            // 현재 ruleVersionId에 해당하는 카테고리만 필터링
            List<AccountCategoryEntity> currentVersionCategories = company.getCategories().stream()
                    .filter(category -> ruleVersionId.equals(category.getRuleVersionId()))
                    .toList();
                    
            log.debug("회사 처리 중: {} (전체 카테고리: {}개, 현재 버전 카테고리: {}개)", 
                    company.getCompanyName(), company.getCategories().size(), currentVersionCategories.size());
            
            for (AccountCategoryEntity category : currentVersionCategories) {
                // 키워드들을 포함 키워드로 변환
                List<String> keywords = category.getKeywords().stream()
                        .map(CategoryKeywordEntity::getKeyword)
                        .toList();
                
                if (!keywords.isEmpty()) {
                    // 중복 검사를 위한 고유 키 생성
                    String ruleKey = company.getId() + "-" + category.getId();
                    
                    if (ruleKeys.contains(ruleKey)) {
                        log.warn("중복된 규칙 발견, 건너뜀: {} - {} (키: {})", 
                                company.getCompanyName(), category.getCategoryName(), ruleKey);
                        continue;
                    }
                    
                    ruleKeys.add(ruleKey);
                    
                    MatchingRule rule = MatchingRule.builder()
                            .companyId(company.getId())
                            .companyName(company.getCompanyName())
                            .categoryId(category.getId())
                            .categoryName(category.getCategoryName())
                            .includeKeywords(keywords)
                            .excludeKeywords(category.getExcludeKeywords() != null ? 
                                    category.getExcludeKeywords() : Collections.emptyList())
                            .minAmount(category.getMinAmount())
                            .maxAmount(category.getMaxAmount())
                            .priority(category.getPriority() != null ? category.getPriority() : 0)
                            .build();
                    
                    rules.add(rule);
                    log.debug("매칭 규칙 추가: {} - {} (키워드: {}, 우선순위: {}, 버전: {})", 
                            company.getCompanyName(), category.getCategoryName(), keywords, rule.getPriority(), ruleVersionId);
                }
            }
        }
        
        log.info("총 {}개 매칭 규칙 생성 완료 (Rule Version: {})", rules.size(), ruleVersionId);
        return rules;
    }
    
    private CompanyEntity findCompanyById(List<CompanyEntity> companies, UUID companyId) {
        return companies.stream()
                .filter(c -> c.getId().equals(companyId))
                .findFirst()
                .orElse(null);
    }
    
    private AccountCategoryEntity findCategoryById(List<CompanyEntity> companies, UUID categoryId) {
        return companies.stream()
                .flatMap(c -> c.getCategories().stream())
                .filter(cat -> cat.getId().equals(categoryId))
                .findFirst()
                .orElse(null);
    }
    
    private CompanyEntity getOrCreateCompany(String companyId, String companyName) {
        // 기존 회사 조회
        Optional<CompanyEntity> existingCompany = companyRepository.findByCompanyId(companyId);
        
        if (existingCompany.isPresent()) {
            log.debug("기존 회사 사용: {}", companyId);
            return existingCompany.get();
        }
        
        // 새로운 회사 생성
        CompanyEntity newCompany = CompanyEntity.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .companyName(companyName)
                .categories(new ArrayList<>())
                .build();
        
        CompanyEntity savedCompany = companyRepository.save(newCompany);
        log.info("새로운 회사 생성: {} (ID: {})", companyId, savedCompany.getId());
        return savedCompany;
    }
    
    private List<CompanyEntity> saveRulesToDatabaseAndReturn(CompanyRulesDto rules, UUID ruleVersionId) {
        List<CompanyEntity> savedCompanies = new ArrayList<>();
        
        for (CompanyRulesDto.CompanyDto companyDto : rules.getCompanies()) {
            // 1. 기존 회사 확인 및 생성/조회
            CompanyEntity company = getOrCreateCompany(companyDto.getCompany_id(), companyDto.getCompany_name());
            
            // 2. 각 카테고리 처리 (rule_version별로 별도 관리)
            List<AccountCategoryEntity> savedCategories = new ArrayList<>();
            for (CompanyRulesDto.CategoryDto categoryDto : companyDto.getCategories()) {
                // 동일한 company_id, category_id, rule_version_id 조합이 있는지 확인
                Optional<AccountCategoryEntity> existingCategory = accountCategoryRepository
                        .findByCategoryIdAndCompanyIdAndRuleVersionId(
                                categoryDto.getCategory_id(), 
                                company.getId(), 
                                ruleVersionId);
                
                if (existingCategory.isPresent()) {
                    log.info("카테고리 '{}' (회사: {}, 규칙버전: {})가 이미 존재합니다. 중복 저장을 생략합니다.", 
                            categoryDto.getCategory_id(), companyDto.getCompany_id(), ruleVersionId);
                    savedCategories.add(existingCategory.get());
                    continue; // 기존 카테고리가 있으면 생략
                }
                
                // 새로운 카테고리 생성
                AccountCategoryEntity category = AccountCategoryEntity.builder()
                        .id(UUID.randomUUID())
                        .categoryId(categoryDto.getCategory_id())
                        .categoryName(categoryDto.getCategory_name())
                        .company(company)
                        .ruleVersionId(ruleVersionId)
                        .keywords(new ArrayList<>())
                        .excludeKeywords(categoryDto.getExclude_keywords())
                        .minAmount(categoryDto.getMin_amount())
                        .maxAmount(categoryDto.getMax_amount())
                        .priority(categoryDto.getPriority() != null ? categoryDto.getPriority() : 0)
                        .build();
                
                // 키워드 추가
                for (String keyword : categoryDto.getKeywords()) {
                    CategoryKeywordEntity keywordEntity = CategoryKeywordEntity.builder()
                            .id(UUID.randomUUID())
                            .keyword(keyword)
                            .category(category)
                            .build();
                    category.getKeywords().add(keywordEntity);
                }
                
                // 카테고리 저장
                AccountCategoryEntity savedCategory = accountCategoryRepository.save(category);
                savedCategories.add(savedCategory);
            }
            
            // 회사에 카테고리 설정
            company.getCategories().clear();
            company.getCategories().addAll(savedCategories);
            savedCompanies.add(company);
        }
        
        log.info("규칙 버전 {}에 대한 {}개 회사 데이터 저장 완료", ruleVersionId, rules.getCompanies().size());
        return savedCompanies;
    }
    
    // 기존 메서드 호환성을 위해 유지
    private void saveRulesToDatabase(CompanyRulesDto rules, UUID ruleVersionId) {
        saveRulesToDatabaseAndReturn(rules, ruleVersionId);
    }
    
    private RuleVersionEntity saveRulesAsVersion(CompanyRulesDto rules) {
        try {
            // CompanyRulesDto를 JsonNode로 변환
            JsonNode ruleData = objectMapper.valueToTree(rules);
            
            // 버전명 자동 생성
            String versionName = "v" + java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            
            // 규칙 버전 엔티티 생성 및 저장
            RuleVersionEntity ruleVersion = RuleVersionEntity.builder()
                    .versionName(versionName)
                    .ruleData(ruleData)
                    .description("자동 생성된 규칙 버전 (거래 처리시)")
                    .active(true)
                    .isConsumed(false)
                    .uploadedAt(LocalDateTime.now())
                    .usedAt(LocalDateTime.now())
                    .build();
            
            RuleVersionEntity saved = ruleVersionService.saveRuleVersion(ruleVersion);
            log.info("규칙 버전 자동 저장 완료: {}", versionName);
            return saved;
            
        } catch (Exception e) {
            log.error("규칙 버전 저장 실패", e);
            throw new RuntimeException("규칙 버전 저장 중 오류가 발생했습니다.", e);
        }
    }
    
    private void saveClassificationAndEvent(TransactionClassification classification, TransactionProcessedEvent event, UUID ruleVersionId) {
        // 분류 결과 저장
        TransactionClassificationEntity classificationEntity = TransactionClassificationEntity.builder()
                .id(classification.getId())
                .transactionId(classification.getTransactionId())
                .companyId(classification.getCompanyId())
                .companyName(classification.getCompanyName())
                .categoryId(classification.getCategoryId())
                .categoryName(classification.getCategoryName())
                .matchedKeyword(classification.getMatchedKeyword())
                .status(classification.getStatus())
                .classifiedAt(classification.getClassifiedAt())
                .ruleVersionId(ruleVersionId)
                .build();
        
        classificationRepository.save(classificationEntity);
        
        // 이벤트 저장
        TransactionEventEntity eventEntity = TransactionEventEntity.builder()
                .eventId(event.getEventId())
                .transactionId(event.getTransactionId())
                .companyId(event.getCompanyId())
                .categoryId(event.getCategoryId())
                .matchedKeyword(event.getMatchedKeyword())
                .status(event.getStatus())
                .occurredAt(event.getOccurredAt())
                .eventType(event.getEventType())
                .build();
        
        eventRepository.save(eventEntity);
    }
    
    private Company mapToCompanyDomain(CompanyEntity entity) {
        return Company.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .companyName(entity.getCompanyName())
                .build();
    }
    
    private AccountCategory mapToCategoryDomain(AccountCategoryEntity entity, String matchedKeyword) {
        List<String> keywords = entity.getKeywords().stream()
                .map(CategoryKeywordEntity::getKeyword)
                .toList();
        
        return AccountCategory.builder()
                .id(entity.getId())
                .categoryId(entity.getCategoryId())
                .categoryName(entity.getCategoryName())
                .keywords(keywords)
                .companyId(entity.getCompany().getId())
                .ruleVersionId(entity.getRuleVersionId())
                .build();
    }
    
    private record ClassificationResult(
            CompanyEntity company, 
            AccountCategoryEntity category, 
            String matchedKeyword) {}
}