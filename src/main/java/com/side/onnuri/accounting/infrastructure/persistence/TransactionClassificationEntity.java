package com.side.onnuri.accounting.infrastructure.persistence;

import com.side.onnuri.accounting.domain.enums.ClassificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transaction_classifications", indexes = {
    @Index(name = "idx_transaction_id", columnList = "transaction_id"),
    @Index(name = "idx_company_id", columnList = "company_id"),
    @Index(name = "idx_classified_at", columnList = "classified_at")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionClassificationEntity {
    
    @Id
    @UuidGenerator
    private UUID id;
    
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;
    
    @Column(name = "company_id")
    private UUID companyId;
    
    @Column(name = "company_name", length = 200)
    private String companyName;
    
    @Column(name = "category_id")
    private UUID categoryId;
    
    @Column(name = "category_name", length = 200)
    private String categoryName;
    
    @Column(name = "matched_keyword", length = 100)
    private String matchedKeyword;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClassificationStatus status;
    
    @Column(name = "classified_at", nullable = false)
    private LocalDateTime classifiedAt;
    
    @Column(name = "rule_version", length = 50)
    private String ruleVersion;
    
    @Column(name = "confidence", precision = 5)
    private Double confidence;
    
    @Column(name = "rule_version_id")
    private UUID ruleVersionId;
    
}