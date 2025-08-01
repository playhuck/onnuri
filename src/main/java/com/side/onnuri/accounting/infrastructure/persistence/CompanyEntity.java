package com.side.onnuri.accounting.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {
    
    @Id
    @UuidGenerator
    private UUID id;
    
    @Column(name = "company_id", unique = true, nullable = false, length = 50)
    private String companyId;
    
    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;
    
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccountCategoryEntity> categories = new ArrayList<>();
}