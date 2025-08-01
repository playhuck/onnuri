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
public class Company {
    private UUID id;
    private String companyId; // com_1, com_2
    private String companyName;
    private List<AccountCategory> categories;
}