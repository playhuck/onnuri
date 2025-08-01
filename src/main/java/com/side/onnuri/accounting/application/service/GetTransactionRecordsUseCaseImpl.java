package com.side.onnuri.accounting.application.service;

import com.side.onnuri.accounting.application.dto.TransactionRecordResponse;
import com.side.onnuri.accounting.application.usecase.GetTransactionRecordsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetTransactionRecordsUseCaseImpl implements GetTransactionRecordsUseCase {
    
    private final AccountingQueryServiceImpl queryService;
    
    @Override
    public List<TransactionRecordResponse> execute(String companyId) {
        log.info("사업체별 분류 결과 조회 시작 - companyId: {}", companyId);
        
        // companyId가 없으면 빈 리스트 반환
        if (companyId == null || companyId.trim().isEmpty()) {
            log.warn("companyId가 제공되지 않았습니다. 빈 결과를 반환합니다.");
            return List.of();
        }
        
        List<TransactionRecordResponse> records = queryService.getTransactionRecordsByCompanyId(companyId);
        
        log.info("사업체별 분류 결과 조회 완료 - companyId: {}, 건수: {}", companyId, records.size());
        return records;
    }
}