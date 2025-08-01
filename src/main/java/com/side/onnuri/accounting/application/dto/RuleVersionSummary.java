package com.side.onnuri.accounting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleVersionSummary {
    
    private UUID ruleVersionId;
    private String versionName;
    private LocalDateTime uploadedAt;
    private LocalDateTime usedAt;
    private Boolean active;
    private Boolean isConsumed;
    private String description;
    private Boolean isAvailable;
}