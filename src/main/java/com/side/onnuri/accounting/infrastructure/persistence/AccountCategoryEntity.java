package com.side.onnuri.accounting.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "account_categories")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCategoryEntity {
    
    @Id
    @UuidGenerator
    private UUID id;
    
    @Column(name = "category_id", nullable = false, length = 50)
    private String categoryId;
    
    @Column(name = "category_name", nullable = false, length = 200)
    private String categoryName;
    
    @Column(name = "rule_version_id", nullable = false)
    private UUID ruleVersionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;
    
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CategoryKeywordEntity> keywords = new ArrayList<>();
    
    // 고급 매칭 조건들
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exclude_keywords", columnDefinition = "jsonb")
    private List<String> excludeKeywords;
    
    @Column(name = "min_amount", precision = 19, scale = 2)
    private BigDecimal minAmount;
    
    @Column(name = "max_amount", precision = 19, scale = 2)
    private BigDecimal maxAmount;
    
    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;
}