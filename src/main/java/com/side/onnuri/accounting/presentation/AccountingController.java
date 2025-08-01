package com.side.onnuri.accounting.presentation;

import com.side.onnuri.accounting.application.dto.ProcessResponse;
import com.side.onnuri.accounting.application.dto.TransactionRecordResponse;
import com.side.onnuri.accounting.application.usecase.GetTransactionRecordsUseCase;
import com.side.onnuri.accounting.application.usecase.ProcessTransactionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounting")
@RequiredArgsConstructor
@Slf4j
public class AccountingController {
    
    private final ProcessTransactionUseCase processTransactionUseCase;
    private final GetTransactionRecordsUseCase getTransactionRecordsUseCase;
    
    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProcessResponse> processTransactions(
            @RequestParam(value = "bank_transactions", required = false) MultipartFile bankTransactionsCsv,
            @RequestParam(value = "rules", required = false) MultipartFile rulesJson) {
        
        ProcessResponse response = processTransactionUseCase.execute(bankTransactionsCsv, rulesJson);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/records")
    public ResponseEntity<List<TransactionRecordResponse>> getTransactionRecords(
            @RequestParam(value = "companyId", required = false) String companyId) {
        
        List<TransactionRecordResponse> records = getTransactionRecordsUseCase.execute(companyId);
        return ResponseEntity.ok(records);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Accounting Service is running");
    }
}