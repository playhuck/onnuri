package com.side.onnuri.accounting.infrastructure.repository;

import com.side.onnuri.accounting.infrastructure.persistence.TransactionEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionEventRepository extends JpaRepository<TransactionEventEntity, UUID> {
    
    List<TransactionEventEntity> findByTransactionIdOrderByOccurredAtDesc(UUID transactionId);
    
    @Query("SELECT te FROM TransactionEventEntity te WHERE te.occurredAt BETWEEN :startDate AND :endDate ORDER BY te.occurredAt DESC")
    List<TransactionEventEntity> findEventsBetweenDates(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT te FROM TransactionEventEntity te WHERE te.eventType = :eventType ORDER BY te.occurredAt DESC")
    List<TransactionEventEntity> findByEventType(@Param("eventType") String eventType);
}