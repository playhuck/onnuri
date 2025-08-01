package com.side.onnuri.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessResponse {
    private int totalTransactions;
    private int classifiedTransactions;
    private int unclassifiedTransactions;
    private String message;
    
    public static ProcessResponse of(int total, int classified, int unclassified) {
        return ProcessResponse.builder()
                .totalTransactions(total)
                .classifiedTransactions(classified)
                .unclassifiedTransactions(unclassified)
                .message(String.format("총 %d건 처리완료 (분류: %d건, 미분류: %d건)", 
                        total, classified, unclassified))
                .build();
    }
}