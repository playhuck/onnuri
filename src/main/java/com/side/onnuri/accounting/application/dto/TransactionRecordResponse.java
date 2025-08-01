package com.side.onnuri.accounting.application.dto;

import com.side.onnuri.accounting.domain.enums.ClassificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecordResponse {
    private UUID transactionId;
    private LocalDateTime transactionDateTime;
    private String description;
    private BigDecimal depositAmount;
    private BigDecimal withdrawalAmount;
    private BigDecimal balanceAfter;
    private String branchName;
    
    private String companyId;
    private String companyName;
    private String categoryId;
    private String categoryName;
    private String matchedKeyword;
    private ClassificationStatus status;
    private LocalDateTime classifiedAt;
}