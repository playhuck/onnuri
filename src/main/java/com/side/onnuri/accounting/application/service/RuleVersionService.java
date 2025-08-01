package com.side.onnuri.accounting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.onnuri.accounting.application.dto.CompanyRulesDto;
import com.side.onnuri.accounting.infrastructure.persistence.RuleVersionEntity;
import com.side.onnuri.accounting.infrastructure.repository.RuleVersionRepository;
import com.side.onnuri.common.exception.ErrorCode;
import com.side.onnuri.common.exception.EntityNotFoundException;
import com.side.onnuri.common.exception.FileProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RuleVersionService {
    
    private final RuleVersionRepository ruleVersionRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 새로운 규칙 버전 업로드
     */
    @Transactional
    public RuleVersionEntity uploadNewRuleVersion(MultipartFile jsonFile, String description) {
        try {
            // JSON 파일 파싱
            JsonNode ruleData = objectMapper.readTree(jsonFile.getInputStream());
            
            // 버전명 자동 생성 (타임스탬프 기반)
            String versionName = generateVersionName();
            
            // 규칙 버전 엔티티 생성
            RuleVersionEntity ruleVersion = RuleVersionEntity.builder()
                    .versionName(versionName)
                    .ruleData(ruleData)
                    .description(description)
                    .active(true)
                    .isConsumed(false)
                    .build();
            
            RuleVersionEntity saved = ruleVersionRepository.save(ruleVersion);
            
            log.info("새로운 규칙 버전 업로드 완료: {} (ID: {})", versionName, saved.getId());
            return saved;
            
        } catch (Exception e) {
            log.error("규칙 버전 업로드 실패", e);
            throw new FileProcessingException(ErrorCode.JSON_PARSING_ERROR, "규칙 파일 업로드 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 사용 가능한 활성 규칙 조회
     */
    @Transactional(readOnly = true)
    public Optional<RuleVersionEntity> findActiveAvailableRule() {
        return ruleVersionRepository.findActiveAvailableRule();
    }
    
    /**
     * 규칙을 사용된 것으로 마킹
     */
    @Transactional
    public void markRuleAsUsed(UUID ruleVersionId) {
        RuleVersionEntity ruleVersion = ruleVersionRepository.findById(ruleVersionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "규칙 버전을 찾을 수 없습니다."));
        
        ruleVersion.markAsUsed();
        ruleVersionRepository.save(ruleVersion);
        
        log.info("규칙 버전 {}을 사용된 것으로 마킹", ruleVersion.getVersionName());
    }
    
    /**
     * 규칙을 사용된 것으로 마킹하고 비활성화
     */
    @Transactional
    public void markRuleAsUsedAndDeactivate(UUID ruleVersionId) {
        RuleVersionEntity ruleVersion = ruleVersionRepository.findById(ruleVersionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "규칙 버전을 찾을 수 없습니다."));
        
        ruleVersion.markAsUsed();
        ruleVersion.deactivate();
        ruleVersionRepository.save(ruleVersion);
        
        log.info("규칙 버전 {}을 사용 완료 및 비활성화", ruleVersion.getVersionName());
    }
    
    /**
     * 특정 규칙 버전 재활성화
     */
    @Transactional
    public void reactivateRule(UUID ruleVersionId) {
        RuleVersionEntity ruleVersion = ruleVersionRepository.findById(ruleVersionId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.ENTITY_NOT_FOUND, "규칙 버전을 찾을 수 없습니다."));
        
        // 모든 규칙 비활성화 후 선택된 규칙만 활성화
        ruleVersionRepository.deactivateAllRules();
        ruleVersion.activate();
        ruleVersionRepository.save(ruleVersion);
        
        log.info("규칙 버전 {} 재활성화", ruleVersion.getVersionName());
    }
    
    /**
     * 모든 규칙 버전 조회
     */
    @Transactional(readOnly = true)
    public List<RuleVersionEntity> findAllRuleVersions() {
        return ruleVersionRepository.findAll();
    }
    
    /**
     * 사용된 규칙들 조회
     */
    @Transactional(readOnly = true)
    public List<RuleVersionEntity> findUsedRules() {
        return ruleVersionRepository.findAllUsedRules();
    }
    
    /**
     * 규칙 데이터를 CompanyRulesDto로 변환
     */
    public CompanyRulesDto convertToCompanyRulesDto(JsonNode ruleData) {
        try {
            return objectMapper.treeToValue(ruleData, CompanyRulesDto.class);
        } catch (Exception e) {
            log.error("규칙 데이터 변환 실패", e);
            throw new FileProcessingException(ErrorCode.JSON_PARSING_ERROR, "규칙 데이터 변환 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 규칙 버전 엔티티 직접 저장 (내부 사용)
     */
    @Transactional
    public RuleVersionEntity saveRuleVersion(RuleVersionEntity ruleVersion) {
        return ruleVersionRepository.save(ruleVersion);
    }
    
    private String generateVersionName() {
        return "v" + java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    }
}