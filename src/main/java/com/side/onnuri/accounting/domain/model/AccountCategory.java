package com.side.onnuri.accounting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCategory {
    private UUID id;
    private String categoryId; // cat_101, cat_201
    private String categoryName;
    private List<String> keywords;
    private UUID companyId;
    private UUID ruleVersionId;

}