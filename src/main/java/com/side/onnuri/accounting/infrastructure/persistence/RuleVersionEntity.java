package com.side.onnuri.accounting.infrastructure.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rule_versions", indexes = {
    @Index(name = "idx_active", columnList = "active"),
    @Index(name = "idx_is_consumed", columnList = "is_consumed"),
    @Index(name = "idx_uploaded_at", columnList = "uploaded_at")
})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleVersionEntity {
    
    @Id
    @UuidGenerator
    private UUID id;
    
    @Column(name = "version_name", nullable = false, length = 100)
    private String versionName;
    
    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rule_data", columnDefinition = "jsonb", nullable = false)
    private JsonNode ruleData;
    
    @Builder.Default
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @Builder.Default
    @Column(name = "is_consumed", nullable = false)
    private Boolean isConsumed = false;
    
    @Column(name = "description", length = 500)
    private String description;
    
    public void markAsUsed() {
        this.usedAt = LocalDateTime.now();
        this.isConsumed = true;
    }
    
    public void activate() {
        this.active = true;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public boolean isAvailable() {
        return active && !isConsumed;
    }
}