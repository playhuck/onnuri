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
public class RuleVersionUploadResponse {
    
    private UUID ruleVersionId;
    private String versionName;
    private LocalDateTime uploadedAt;
    private Boolean active;
    private Boolean isConsumed;
    private String description;
    private String message;
}