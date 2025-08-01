package com.side.onnuri.accounting.domain.event;

import com.side.onnuri.accounting.domain.enums.ClassificationStatus;
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
public class TransactionProcessedEvent {
    private UUID eventId;
    private UUID transactionId;
    private UUID companyId;
    private UUID categoryId;
    private String matchedKeyword;
    private ClassificationStatus status;
    private LocalDateTime occurredAt;
    private String eventType;

    public static TransactionProcessedEvent createClassifiedEvent(
            UUID transactionId, 
            UUID companyId, 
            UUID categoryId, 
            String matchedKeyword) {
        return TransactionProcessedEvent.builder()
                .eventId(UUID.randomUUID())
                .transactionId(transactionId)
                .companyId(companyId)
                .categoryId(categoryId)
                .matchedKeyword(matchedKeyword)
                .status(ClassificationStatus.CLASSIFIED)
                .occurredAt(LocalDateTime.now())
                .eventType("TRANSACTION_CLASSIFIED")
                .build();
    }

    public static TransactionProcessedEvent createUnclassifiedEvent(UUID transactionId) {
        return TransactionProcessedEvent.builder()
                .eventId(UUID.randomUUID())
                .transactionId(transactionId)
                .status(ClassificationStatus.UNCLASSIFIED)
                .occurredAt(LocalDateTime.now())
                .eventType("TRANSACTION_UNCLASSIFIED")
                .build();
    }
}