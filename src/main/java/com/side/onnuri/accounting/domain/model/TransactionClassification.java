package com.side.onnuri.accounting.domain.model;

import com.side.onnuri.accounting.domain.enums.ClassificationStatus;
import com.side.onnuri.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionClassification {
    private UUID id;
    private UUID transactionId;
    private UUID companyId;
    private String companyName;
    private UUID categoryId;
    private String categoryName;
    private String matchedKeyword;
    private ClassificationStatus status;
    private LocalDateTime classifiedAt;

    public static TransactionClassification createClassified(
            UUID transactionId, 
            Company company, 
            AccountCategory category, 
            String matchedKeyword) {
        return TransactionClassification.builder()
                .id(UUID.randomUUID())
                .transactionId(transactionId)
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .categoryId(category.getId())
                .categoryName(category.getCategoryName())
                .matchedKeyword(matchedKeyword)
                .status(ClassificationStatus.CLASSIFIED)
                .classifiedAt(LocalDateTime.now())
                .build();
    }

    public static TransactionClassification createUnclassified(UUID transactionId) {
        return TransactionClassification.builder()
                .id(UUID.randomUUID())
                .transactionId(transactionId)
                .companyId(Constants.Un_Classified_Company_Id)
                .categoryId(Constants.Un_Classified_Category_Id)
                .status(ClassificationStatus.UNCLASSIFIED)
                .companyName(Constants.Un_Classified_Company)
                .categoryName(Constants.Un_Classified_Category)
                .matchedKeyword(Constants.Un_Classified_Keyword)
                .classifiedAt(LocalDateTime.now())
                .build();
    }

    public boolean isClassified() {
        return status == ClassificationStatus.CLASSIFIED;
    }
}