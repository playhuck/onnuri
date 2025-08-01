package com.side.onnuri.accounting.application.service;

import com.side.onnuri.accounting.application.dto.TransactionRecordResponse;
import com.side.onnuri.accounting.infrastructure.persistence.BankTransactionEntity;
import com.side.onnuri.accounting.infrastructure.persistence.TransactionClassificationEntity;
import com.side.onnuri.accounting.infrastructure.repository.BankTransactionRepository;
import com.side.onnuri.accounting.infrastructure.repository.TransactionClassificationRepository;
import com.side.onnuri.common.exception.ErrorCode;
import com.side.onnuri.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountingQueryServiceImpl {
    
    private final TransactionClassificationRepository classificationRepository;
    private final BankTransactionRepository bankTransactionRepository;
    
    public List<TransactionRecordResponse> getTransactionRecordsByCompanyId(String companyId) {
        if (!StringUtils.hasText(companyId)) {
            throw new EntityNotFoundException(ErrorCode.INVALID_COMPANY_ID, "회사 ID는 필수입니다.");
        }
        
        List<TransactionClassificationEntity> classifications = 
                classificationRepository.findClassifiedTransactionsByCompanyIdString(companyId);
        
        if (classifications.isEmpty()) {
            log.info("회사 ID: {}에 대한 분류된 거래 기록이 없습니다.", companyId);
            return List.of();
        }
        
        // 거래 ID 목록 추출
        List<UUID> transactionIds = classifications.stream()
                .map(TransactionClassificationEntity::getTransactionId)
                .toList();
        
        // 거래 정보 조회
        List<BankTransactionEntity> transactions = bankTransactionRepository.findAllById(transactionIds);
        Map<UUID, BankTransactionEntity> transactionMap = transactions.stream()
                .collect(Collectors.toMap(BankTransactionEntity::getId, t -> t));
        
        // 응답 생성
        List<TransactionRecordResponse> responses = classifications.stream()
                .map(classification -> {
                    BankTransactionEntity transaction = transactionMap.get(classification.getTransactionId());
                    if (transaction == null) {
                        log.warn("거래 정보를 찾을 수 없습니다: {}", classification.getTransactionId());
                        return null;
                    }
                    
                    return TransactionRecordResponse.builder()
                            .transactionId(transaction.getId())
                            .transactionDateTime(extractTransactionDateTime(transaction))
                            .description(extractDescription(transaction))
                            .depositAmount(extractDepositAmount(transaction))
                            .withdrawalAmount(extractWithdrawalAmount(transaction))
                            .balanceAfter(extractBalanceAfter(transaction))
                            .branchName(extractBranchName(transaction))
                            .companyId(classification.getCompanyId() != null ? classification.getCompanyId().toString() : null)
                            .companyName(classification.getCompanyName())
                            .categoryId(classification.getCategoryId() != null ? classification.getCategoryId().toString() : null)
                            .categoryName(classification.getCategoryName())
                            .matchedKeyword(classification.getMatchedKeyword())
                            .status(classification.getStatus())
                            .classifiedAt(classification.getClassifiedAt())
                            .build();
                })
                .filter(response -> response != null)
                .toList();
        
        log.info("회사 ID: {}에 대한 {}건의 거래 기록 조회 완료", companyId, responses.size());
        return responses;
    }
    
    private LocalDateTime extractTransactionDateTime(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        String[] possibleKeys = {"거래일시", "transaction_date", "date", "거래일", "일시"};
        
        for (String key : possibleKeys) {
            Object value = attributes.get(key);
            if (value instanceof LocalDateTime) {
                return (LocalDateTime) value;
            } else if (value instanceof String) {
                try {
                    return LocalDateTime.parse((String) value, 
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e) {
                    log.warn("날짜 문자열 파싱 실패: {}", value);
                }
            }
        }
        return null;
    }
    
    private String extractDescription(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        String[] possibleKeys = {"적요", "description", "memo", "거래내용", "내용"};
        
        for (String key : possibleKeys) {
            Object value = attributes.get(key);
            if (value instanceof String && !((String) value).trim().isEmpty()) {
                return (String) value;
            }
        }
        return "";
    }
    
    private BigDecimal extractDepositAmount(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        String[] possibleKeys = {"입금액", "deposit", "deposit_amount", "입금"};
        
        for (String key : possibleKeys) {
            Object value = attributes.get(key);
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
        }
        return BigDecimal.ZERO;
    }
    
    private BigDecimal extractWithdrawalAmount(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        String[] possibleKeys = {"출금액", "withdrawal", "withdrawal_amount", "출금"};
        
        for (String key : possibleKeys) {
            Object value = attributes.get(key);
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
        }
        return BigDecimal.ZERO;
    }
    
    private BigDecimal extractBalanceAfter(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        String[] possibleKeys = {"거래후잔액", "balance", "balance_after", "잔액", "거래후_잔액"};
        
        for (String key : possibleKeys) {
            Object value = attributes.get(key);
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
        }
        return BigDecimal.ZERO;
    }
    
    private String extractBranchName(BankTransactionEntity transaction) {
        Map<String, Object> attributes = transaction.getAttributes();
        String[] possibleKeys = {"거래점", "branch", "branch_name", "지점", "거래지점"};
        
        for (String key : possibleKeys) {
            Object value = attributes.get(key);
            if (value instanceof String && !((String) value).trim().isEmpty()) {
                return (String) value;
            }
        }
        return "";
    }
}