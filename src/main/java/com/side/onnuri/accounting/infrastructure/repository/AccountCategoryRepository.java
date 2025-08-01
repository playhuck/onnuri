package com.side.onnuri.accounting.infrastructure.repository;

import com.side.onnuri.accounting.infrastructure.persistence.AccountCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountCategoryRepository extends JpaRepository<AccountCategoryEntity, UUID> {
    
    @Query("SELECT DISTINCT ac FROM AccountCategoryEntity ac LEFT JOIN FETCH ac.keywords WHERE ac.id IN :categoryIds")
    List<AccountCategoryEntity> findCategoriesWithKeywords(@Param("categoryIds") List<UUID> categoryIds);
    
    Optional<AccountCategoryEntity> findByCategoryIdAndCompanyIdAndRuleVersionId(
            String categoryId, UUID companyId, UUID ruleVersionId);

}