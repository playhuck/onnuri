package com.side.onnuri.accounting.infrastructure.mapper;

import com.side.onnuri.accounting.domain.model.BankTransaction;
import com.side.onnuri.accounting.infrastructure.persistence.BankTransactionEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.UUID;

@Component
public class BankTransactionMapper {
    
    public BankTransactionEntity toEntity(BankTransaction domain) {
        if (domain == null) {
            return null;
        }
        
        return BankTransactionEntity.builder()
                .id(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .attributes(new HashMap<>(domain.getAttributes()))
                .createdAt(domain.getCreatedAt())
                .build();
    }

}