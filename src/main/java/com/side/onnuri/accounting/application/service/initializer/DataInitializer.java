package com.side.onnuri.accounting.application.service.initializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.side.onnuri.accounting.infrastructure.persistence.AccountCategoryEntity;
import com.side.onnuri.accounting.infrastructure.persistence.CompanyEntity;
import com.side.onnuri.accounting.infrastructure.persistence.RuleVersionEntity;
import com.side.onnuri.accounting.infrastructure.repository.AccountCategoryRepository;
import com.side.onnuri.accounting.infrastructure.repository.CompanyRepository;
import com.side.onnuri.accounting.infrastructure.repository.RuleVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.side.onnuri.common.Constants.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CompanyRepository companyRepository;
    private final RuleVersionRepository ruleVersionRepository;
    private final AccountCategoryRepository accountCategoryRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void addInitialData() {

        log.info("Company & Rule,미분류 data Initializing 시작");

        CompanyEntity company = companyRepository.save(
                CompanyEntity
                        .builder()
                        .id(UUID.randomUUID())
                        .companyName(Un_Classified_Company)
                        .companyId(Un_Classified_Company)
                        .build()
        );

        Un_Classified_Company_Id = company.getId();

        LocalDateTime now = LocalDateTime.now();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.createObjectNode();

        RuleVersionEntity ruleVersion = ruleVersionRepository.save(
                RuleVersionEntity
                        .builder()
                        .usedAt(now)
                        .uploadedAt(now)
                        .active(false)
                        .ruleData(node)
                        .versionName(Un_Classified_Company_Rule_Version_Name)
                        .id(UUID.randomUUID())
                        .isConsumed(true)
                        .description(Un_Classified_Company_Rule_Description)
                        .build()
        );

        Un_Classified_Company_Rule_Id = ruleVersion.getId();

        AccountCategoryEntity accountCategory = accountCategoryRepository.save(
                AccountCategoryEntity
                        .builder()
                        .company(company)
                        .id(UUID.randomUUID())
                        .ruleVersionId(ruleVersion.getId())
                        .categoryName(Un_Classified_Category)
                        .categoryId(Un_Classified_Category_Formal_Id)
                        .build()
        );

        Un_Classified_Category_Id = accountCategory.getId();

        log.info("Company & Rule,미분류 data Initializing 성공");
    }

}
