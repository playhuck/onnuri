package com.side.onnuri.accounting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransaction {
    private UUID id;
    
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
    
    private LocalDateTime createdAt;

    public <T> T getAttribute(String key, Class<T> type) {
        Object value = attributes.get(key);
        if (value == null) {
            return null;
        }
        return type.cast(value);
    }

    public String getDescription() {
        return getAttribute("적요", String.class);
    }

    public BigDecimal getDepositAmount() {
        return getAttribute("입금액", BigDecimal.class);
    }

    public BigDecimal getWithdrawalAmount() {
        return getAttribute("출금액", BigDecimal.class);
    }

    public BigDecimal getBalanceAfter() {
        return getAttribute("거래후잔액", BigDecimal.class);
    }

    public String getBranchName() {
        return getAttribute("거래점", String.class);
    }

    public BigDecimal getTransactionAmount() {
        BigDecimal deposit = getDepositAmount();
        BigDecimal withdrawal = getWithdrawalAmount();
        
        if (deposit != null && deposit.compareTo(BigDecimal.ZERO) > 0) {
            return deposit;
        }
        if (withdrawal != null && withdrawal.compareTo(BigDecimal.ZERO) > 0) {
            return withdrawal;
        }
        return BigDecimal.ZERO;
    }

    public boolean isDeposit() {
        BigDecimal deposit = getDepositAmount();
        return deposit != null && deposit.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isWithdrawal() {
        BigDecimal withdrawal = getWithdrawalAmount();
        return withdrawal != null && withdrawal.compareTo(BigDecimal.ZERO) > 0;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
}