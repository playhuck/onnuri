package com.side.onnuri.accounting.infrastructure.repository;

import com.side.onnuri.accounting.infrastructure.persistence.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, UUID> {
    
    Optional<CompanyEntity> findByCompanyId(String companyId);
    
    @Query("SELECT DISTINCT c FROM CompanyEntity c LEFT JOIN FETCH c.categories cat WHERE cat.ruleVersionId = :ruleVersionId")
    List<CompanyEntity> findAllByRuleVersionIdWithCategories(@Param("ruleVersionId") UUID ruleVersionId);
}