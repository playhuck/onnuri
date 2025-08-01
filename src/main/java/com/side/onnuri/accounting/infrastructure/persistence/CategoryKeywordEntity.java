package com.side.onnuri.accounting.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "category_keywords")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryKeywordEntity {
    
    @Id
    @UuidGenerator
    private UUID id;
    
    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private AccountCategoryEntity category;
}