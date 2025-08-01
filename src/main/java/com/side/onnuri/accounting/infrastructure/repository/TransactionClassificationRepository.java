package com.side.onnuri.accounting.infrastructure.repository;

import com.side.onnuri.accounting.infrastructure.persistence.TransactionClassificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionClassificationRepository extends JpaRepository<TransactionClassificationEntity, UUID> {
    
    Optional<TransactionClassificationEntity> findByTransactionId(UUID transactionId);
    
    List<TransactionClassificationEntity> findByCompanyIdOrderByClassifiedAtDesc(UUID companyId);
    
    @Query("""
        SELECT tc FROM TransactionClassificationEntity tc 
        WHERE tc.companyId = :companyId 
        ORDER BY tc.classifiedAt DESC
        """)
    List<TransactionClassificationEntity> findClassifiedTransactionsByCompanyId(@Param("companyId") UUID companyId);
    
    @Query("""
        SELECT tc FROM TransactionClassificationEntity tc 
        JOIN CompanyEntity c ON tc.companyId = c.id 
        WHERE c.companyId = :companyId 
        ORDER BY tc.classifiedAt DESC
        """)
    List<TransactionClassificationEntity> findClassifiedTransactionsByCompanyIdString(@Param("companyId") String companyId);
}