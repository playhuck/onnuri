package com.side.onnuri.accounting.application.service.matching;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeywordMatch {
    
    private String keyword;           // 매칭된 키워드
    private UUID companyId;          // 회사 ID
    private UUID categoryId;         // 카테고리 ID
    private int startPosition;       // 시작 위치
    private int endPosition;         // 끝 위치
    private int length;              // 키워드 길이
    
    /**
     * 적합도 계산 (키워드 길이 기반)
     */
    public double calculateRelevance(String fullText) {
        if (fullText == null || fullText.trim().isEmpty()) {
            return 0.0;
        }
        return (double) length / fullText.length();
    }
}