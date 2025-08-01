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
@Table(name = "transaction_events", indexes = {
    @Index(name = "idx_transaction_id_event", columnList = "transaction_id"),
    @Index(name = "idx_occurred_at", columnList = "occurred_at"),
    @Index(name = "idx_event_type", columnList = "event_type")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEventEntity {
    
    @Id
    @UuidGenerator
    private UUID eventId;
    
    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;
    
    @Column(name = "company_id")
    private UUID companyId;
    
    @Column(name = "category_id")
    private UUID categoryId;
    
    @Column(name = "matched_keyword", length = 100)
    private String matchedKeyword;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ClassificationStatus status;
    
    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
}