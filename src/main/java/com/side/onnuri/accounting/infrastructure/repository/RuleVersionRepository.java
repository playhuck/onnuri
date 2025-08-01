package com.side.onnuri.accounting.infrastructure.repository;

import com.side.onnuri.accounting.infrastructure.persistence.RuleVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleVersionRepository extends JpaRepository<RuleVersionEntity, UUID> {

    @Query("SELECT rv FROM RuleVersionEntity rv WHERE rv.active = true AND rv.isConsumed = false ORDER BY rv.uploadedAt DESC")
    Optional<RuleVersionEntity> findActiveAvailableRule();

    @Query("SELECT rv FROM RuleVersionEntity rv WHERE rv.active = true ORDER BY rv.uploadedAt DESC")
    List<RuleVersionEntity> findAllActiveRules();

    Optional<RuleVersionEntity> findByVersionName(String versionName);

    @Query("SELECT rv FROM RuleVersionEntity rv WHERE rv.isConsumed = true ORDER BY rv.usedAt DESC")
    List<RuleVersionEntity> findAllUsedRules();

    @Modifying
    @Query("UPDATE RuleVersionEntity rv SET rv.active = CASE WHEN rv.id = :ruleId THEN true ELSE false END")
    void activateRule(@Param("ruleId") UUID ruleId);

    @Modifying
    @Query("UPDATE RuleVersionEntity rv SET rv.active = false")
    void deactivateAllRules();

    @Query("SELECT rv FROM RuleVersionEntity rv ORDER BY rv.uploadedAt DESC")
    List<RuleVersionEntity> findRecentRules(@Param("limit") int limit);
}