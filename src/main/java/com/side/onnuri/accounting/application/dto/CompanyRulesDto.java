package com.side.onnuri.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRulesDto {
    private List<CompanyDto> companies;
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyDto {
        private String company_id;
        private String company_name;
        private List<CategoryDto> categories;
    }
    
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDto {
        private String category_id;
        private String category_name;
        private List<String> keywords;           // 포함 키워드
        private List<String> exclude_keywords;   // 제외 키워드
        private BigDecimal min_amount;           // 최소 금액
        private BigDecimal max_amount;           // 최대 금액
        private Integer priority;                // 우선순위 (높을수록 우선, null이면 0)
    }
}