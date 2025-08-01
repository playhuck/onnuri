package com.side.onnuri.accounting.presentation;

import com.side.onnuri.accounting.application.dto.RuleVersionSummary;
import com.side.onnuri.accounting.application.dto.RuleVersionUploadResponse;
import com.side.onnuri.accounting.application.service.RuleVersionService;
import com.side.onnuri.accounting.infrastructure.persistence.RuleVersionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
@RequiredArgsConstructor
@Slf4j
public class RuleVersionController {
    
    private final RuleVersionService ruleVersionService;
    
    /**
     * 새로운 규칙 버전 업로드
     */
    @PostMapping("/upload")
    public ResponseEntity<RuleVersionUploadResponse> uploadRule(
            @RequestParam("file") MultipartFile jsonFile,
            @RequestParam(value = "description", required = false) String description) {
        
        log.info("새로운 규칙 업로드 요청: 파일명={}, 설명={}", 
                jsonFile.getOriginalFilename(), description);
        
        RuleVersionEntity ruleVersion = ruleVersionService.uploadNewRuleVersion(jsonFile, description);
        
        RuleVersionUploadResponse response = RuleVersionUploadResponse.builder()
                .ruleVersionId(ruleVersion.getId())
                .versionName(ruleVersion.getVersionName())
                .uploadedAt(ruleVersion.getUploadedAt())
                .active(ruleVersion.getActive())
                .isConsumed(ruleVersion.getIsConsumed())
                .description(ruleVersion.getDescription())
                .message("규칙 버전이 성공적으로 업로드되었습니다.")
                .build();
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 모든 규칙 버전 조회
     */
    @GetMapping
    public ResponseEntity<List<RuleVersionSummary>> getAllRuleVersions() {
        List<RuleVersionEntity> ruleVersions = ruleVersionService.findAllRuleVersions();
        
        List<RuleVersionSummary> summaries = ruleVersions.stream()
                .map(rv -> RuleVersionSummary.builder()
                        .ruleVersionId(rv.getId())
                        .versionName(rv.getVersionName())
                        .uploadedAt(rv.getUploadedAt())
                        .usedAt(rv.getUsedAt())
                        .active(rv.getActive())
                        .isConsumed(rv.getIsConsumed())
                        .description(rv.getDescription())
                        .isAvailable(rv.isAvailable())
                        .build())
                .toList();
        
        return ResponseEntity.ok(summaries);
    }
    
    /**
     * 특정 규칙 버전 재활성화
     */
    @PutMapping("/{ruleId}/activate")
    public ResponseEntity<String> reactivateRule(@PathVariable UUID ruleId) {
        log.info("규칙 재활성화 요청: ruleId={}", ruleId);
        
        ruleVersionService.reactivateRule(ruleId);
        
        return ResponseEntity.ok("규칙이 재활성화되었습니다.");
    }
    
    /**
     * 사용된 규칙들 조회
     */
    @GetMapping("/used")
    public ResponseEntity<List<RuleVersionSummary>> getUsedRules() {
        List<RuleVersionEntity> usedRules = ruleVersionService.findUsedRules();
        
        List<RuleVersionSummary> summaries = usedRules.stream()
                .map(rv -> RuleVersionSummary.builder()
                        .ruleVersionId(rv.getId())
                        .versionName(rv.getVersionName())
                        .uploadedAt(rv.getUploadedAt())
                        .usedAt(rv.getUsedAt())
                        .active(rv.getActive())
                        .isConsumed(rv.getIsConsumed())
                        .description(rv.getDescription())
                        .isAvailable(rv.isAvailable())
                        .build())
                .toList();
        
        return ResponseEntity.ok(summaries);
    }
}