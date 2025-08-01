package com.side.onnuri.accounting.application.service.matching;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvancedTransactionMatcher {
    
    private final KeywordTrie keywordTrie;
    
    /**
     * 매칭 룰들로 Trie 구성
     */
    public void buildTrie(List<MatchingRule> rules) {
        keywordTrie.clear();
        
        int keywordCount = 0;
        for (MatchingRule rule : rules) {
            if (rule.getIncludeKeywords() != null) {
                for (String keyword : rule.getIncludeKeywords()) {
                    keywordTrie.insert(keyword, rule.getCompanyId(), rule.getCategoryId());
                    keywordCount++;
                    log.debug("Trie에 키워드 추가: '{}' -> {}-{} (우선순위: {})", 
                            keyword, rule.getCompanyName(), rule.getCategoryName(), rule.getPriority());
                }
            }
        }
        
        log.info("매칭 규칙 {}개로 Trie 구성 완료 (총 키워드: {}개)", rules.size(), keywordCount);
    }
    
    /**
     * 거래 내역을 매칭 룰들과 비교하여 최적 매칭 결과 반환
     */
    public Optional<MatchingResult> findBestMatch(String description, BigDecimal amount, List<MatchingRule> rules) {
        if (description == null || description.trim().isEmpty()) {
            return Optional.empty();
        }
        
        // 1. Trie로 키워드 매칭
        List<KeywordMatch> keywordMatches = keywordTrie.findMatches(description);
        log.debug("거래 '{}' - Trie에서 찾은 키워드 매칭: {}개", description, keywordMatches.size());
        
        // 2. 각 룰에 대해 매칭 점수 계산
        List<MatchingResult> results = new ArrayList<>();
        
        for (MatchingRule rule : rules) {
            Optional<MatchingResult> result = evaluateRule(description, amount, rule, keywordMatches);
            result.ifPresent(results::add);
        }
        
        // 3. 매칭 결과가 없으면 빈 Optional 반환
        if (results.isEmpty()) {
            log.debug("매칭 결과 없음: description='{}', amount={}", description, amount);
            return Optional.empty();
        }
        
        // 4. 최고 점수 결과들 찾기
        results.sort((r1, r2) -> r1.compareTo(r2));
        MatchingResult best = results.get(0);
        
        // 5. 동점 검사
        List<MatchingResult> topResults = results.stream()
                .filter(r -> r.hasEqualScoreAndPriority(best))
                .collect(Collectors.toList());
        
        if (topResults.size() > 1) {
            // 동점인 경우 에러 발생
            String conflictInfo = topResults.stream()
                    .map(r -> String.format("%s-%s(점수:%d,우선순위:%d)", 
                            r.getCompanyName(), r.getCategoryName(), r.getMatchingScore(), r.getPriority()))
                    .collect(Collectors.joining(", "));
            
            throw new RuntimeException(String.format(
                    "매칭 규칙 충돌 발생 - Description: '%s', Amount: %s, 충돌 규칙: [%s]", 
                    description, amount, conflictInfo));
        }
        
        log.info("매칭 성공: {} -> {}({}) [점수: {}, 우선순위: {}, 적합도: {:.2f}]", 
                description, best.getCompanyName(), best.getCategoryName(), 
                best.getMatchingScore(), best.getPriority(), String.format("%.2f", best.getRelevanceScore()));
        
        return Optional.of(best);
    }
    
    private Optional<MatchingResult> evaluateRule(String description, BigDecimal amount, 
                                                  MatchingRule rule, List<KeywordMatch> keywordMatches) {
        
        int matchingScore = 0;
        List<String> matchedKeywords = new ArrayList<>();
        double totalRelevance = 0.0;
        
        // 1. 포함 키워드 검사
        if (rule.getIncludeKeywords() != null && !rule.getIncludeKeywords().isEmpty()) {
            Set<String> ruleKeywords = new HashSet<>(rule.getIncludeKeywords());
            
            for (KeywordMatch match : keywordMatches) {
                if (match.getCompanyId().equals(rule.getCompanyId()) && 
                    match.getCategoryId().equals(rule.getCategoryId()) &&
                    ruleKeywords.contains(match.getKeyword())) {
                    
                    matchedKeywords.add(match.getKeyword());
                    totalRelevance += match.calculateRelevance(description);
                    matchingScore++;
                }
            }
            
            // 필수 키워드가 하나도 매칭되지 않으면 실패
            if (matchedKeywords.isEmpty()) {
                return Optional.empty();
            }
        }
        
        // 2. 제외 키워드 검사
        if (rule.hasExcludeKeywords()) {
            String lowerDescription = description.toLowerCase();
            for (String excludeKeyword : rule.getExcludeKeywords()) {
                if (lowerDescription.contains(excludeKeyword.toLowerCase())) {
                    log.debug("제외 키워드 '{}' 발견으로 규칙 제외: {}-{}", 
                            excludeKeyword, rule.getCompanyName(), rule.getCategoryName());
                    return Optional.empty();
                }
            }
        }
        
        // 3. 금액 범위 검사
        boolean amountMatched = false;
        if (rule.hasAmountRange() && amount != null) {
            boolean inRange = true;
            
            if (rule.getMinAmount() != null && amount.compareTo(rule.getMinAmount()) < 0) {
                inRange = false;
            }
            if (rule.getMaxAmount() != null && amount.compareTo(rule.getMaxAmount()) > 0) {
                inRange = false;
            }
            
            if (inRange) {
                matchingScore++;
                amountMatched = true;
            } else {
                log.debug("금액 범위 불일치로 규칙 제외: {}-{}, 금액: {}, 범위: {}-{}", 
                        rule.getCompanyName(), rule.getCategoryName(), amount, 
                        rule.getMinAmount(), rule.getMaxAmount());
                return Optional.empty();
            }
        }
        
        // 4. 최종 적합도 계산
        double relevanceScore = matchedKeywords.isEmpty() ? 0.0 : totalRelevance / matchedKeywords.size();
        
        return Optional.of(MatchingResult.builder()
                .companyId(rule.getCompanyId())
                .companyName(rule.getCompanyName())
                .categoryId(rule.getCategoryId())
                .categoryName(rule.getCategoryName())
                .matchingScore(matchingScore)
                .priority(rule.getPriority())
                .matchedKeywords(matchedKeywords)
                .amountMatched(amountMatched)
                .relevanceScore(relevanceScore)
                .build());
    }
}