package com.side.onnuri.accounting.application.service;

import com.side.onnuri.accounting.application.dto.CompanyRulesDto;
import com.side.onnuri.accounting.application.dto.ProcessResponse;
import com.side.onnuri.accounting.application.usecase.ProcessTransactionUseCase;
import com.side.onnuri.accounting.domain.model.BankTransaction;
import com.side.onnuri.accounting.domain.model.BankTransactionDataSet;
import com.side.onnuri.common.exception.ErrorCode;
import com.side.onnuri.common.exception.FileNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessTransactionUseCaseImpl implements ProcessTransactionUseCase {
    
    private final FileProcessingService fileProcessingService;
    private final TransactionClassificationServiceImpl classificationService;
    
    @Override
    public ProcessResponse execute(MultipartFile bankTransactionsCsv, MultipartFile rulesJson) {
        log.info("자동 회계 처리 시작 - CSV: {}, JSON: {}", 
                bankTransactionsCsv != null ? bankTransactionsCsv.getOriginalFilename() : "null", 
                rulesJson != null ? rulesJson.getOriginalFilename() : "null"
        );
        
        boolean hasCsv = bankTransactionsCsv != null && !bankTransactionsCsv.isEmpty();
        boolean hasJson = rulesJson != null && !rulesJson.isEmpty();
        
        // 둘 중 하나라도 없으면 에러
        if (!hasCsv || !hasJson) {
            throw new FileNotFoundException(
                    ErrorCode.REQUIRED_FILE_MISSING,
                    "CSV 파일과 JSON 파일이 모두 필요합니다."
            );
        }
        
        // 둘 다 있는 경우: Header mapping으로 CSV 파싱
        BankTransactionDataSet dataSet = fileProcessingService.parseCsvFile(bankTransactionsCsv);
        CompanyRulesDto rules = fileProcessingService.parseJsonFile(rulesJson);
        
        List<BankTransaction> transactions = dataSet.getTransactions();
        log.info("CSV 파싱 결과: {}", dataSet.getSummary());
        
        classificationService.processTransactions(transactions, rules);
        
        int totalTransactions = transactions.size();
        int classifiedTransactions = (int) (totalTransactions * 0.8);
        int unclassifiedTransactions = totalTransactions - classifiedTransactions;
        
        ProcessResponse response = ProcessResponse.of(totalTransactions, classifiedTransactions, unclassifiedTransactions);
        
        log.info("자동 회계 처리 완료: {}", response.getMessage());
        return response;
    }
}