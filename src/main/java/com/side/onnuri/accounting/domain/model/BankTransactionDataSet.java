package com.side.onnuri.accounting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransactionDataSet {

    private List<BankTransaction> transactions;

    private List<String> headers;

    private ConcurrentHashMap<String, Integer> headerIndexMap;

    private String fileName;
    private LocalDateTime parsedAt;
    private int totalRows;
    private int successfulRows;
    private int failedRows;

    public boolean hasHeader(String headerName) {
        return headerIndexMap != null && headerIndexMap.containsKey(headerName);
    }

    public Integer getHeaderIndex(String headerName) {
        return headerIndexMap != null ? headerIndexMap.get(headerName) : null;
    }

    public double getSuccessRate() {
        return totalRows > 0 ? (double) successfulRows / totalRows * 100.0 : 0.0;
    }

    public String getSummary() {
        return String.format("파일: %s, 전체: %d건, 성공: %d건, 실패: %d건 (성공률: %.1f%%)", 
                fileName, totalRows, successfulRows, failedRows, getSuccessRate());
    }
}