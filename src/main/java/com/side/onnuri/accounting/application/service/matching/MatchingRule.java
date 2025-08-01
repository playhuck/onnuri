package com.side.onnuri.accounting.application.service.matching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchingRule {
    
    private UUID companyId;
    private String companyName;
    private UUID categoryId;
    private String categoryName;
    
    // 키워드 조건
    private List<String> includeKeywords;    // 포함되어야 할 키워드들
    private List<String> excludeKeywords;    // 제외되어야 할 키워드들
    
    // 금액 조건
    private BigDecimal minAmount;            // 최소 금액
    private BigDecimal maxAmount;            // 최대 금액
    
    // 우선순위
    @Builder.Default
    private int priority = 0;                // 높을수록 우선
    
    /**
     * 최대 가능한 매칭 점수 계산
     */
    public int getMaxPossibleScore() {
        int score = 0;
        
        // 키워드 점수
        if (includeKeywords != null) {
            score += includeKeywords.size();
        }
        
        // 금액 범위 점수
        if (minAmount != null || maxAmount != null) {
            score += 1;
        }
        
        return score;
    }
    
    /**
     * 제외 키워드 포함 여부 확인
     */
    public boolean hasExcludeKeywords() {
        return excludeKeywords != null && !excludeKeywords.isEmpty();
    }
    
    /**
     * 금액 범위 조건 포함 여부 확인
     */
    public boolean hasAmountRange() {
        return minAmount != null || maxAmount != null;
    }
}