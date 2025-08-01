package com.side.onnuri.accounting.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.side.onnuri.accounting.application.dto.CompanyRulesDto;
import com.side.onnuri.accounting.domain.model.BankTransaction;
import com.side.onnuri.accounting.domain.model.BankTransactionDataSet;
import com.side.onnuri.common.exception.ErrorCode;
import com.side.onnuri.common.exception.FileProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileProcessingService {

    private final TypeInferenceService typeInferenceService;
    
    public BankTransactionDataSet parseCsvFile(MultipartFile csvFile) {
        if (csvFile == null || csvFile.isEmpty()) {
            throw new FileProcessingException(ErrorCode.EMPTY_FILE, "CSV 파일이 비어있습니다.");
        }
        
        List<BankTransaction> transactions = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        ConcurrentHashMap<String, Integer> headerIndexMap = new ConcurrentHashMap<>();
        Map<String, Class<?>> headerTypes = new HashMap<>();
        int totalRows = 0;
        int successfulRows = 0;
        int failedRows = 0;
        
        try (CSVReader reader = new CSVReader(new InputStreamReader(csvFile.getInputStream(), "UTF-8"))) {
            List<String[]> records = reader.readAll();
            
            if (records.isEmpty()) {
                throw new FileProcessingException(ErrorCode.EMPTY_FILE, "CSV 파일에 데이터가 없습니다.");
            }
            
            // 헤더 파싱
            String[] headerRow = records.getFirst();
            boolean hasHeader = detectHeader(headerRow);
            int startIndex = hasHeader ? 1 : 0;
            
            if (hasHeader) {
                for (int i = 0; i < headerRow.length; i++) {
                    String headerName = headerRow[i].trim();
                    headers.add(headerName);
                    headerIndexMap.put(headerName, i);
                }
                log.info("CSV 헤더 파싱 완료: {}", headers);
            } else {
                // 기본 헤더 생성
                String[] defaultHeaders = {"컬럼0", "컬럼1", "컬럼2", "컬럼3", "컬럼4", "컬럼5"};
                for (int i = 0; i < Math.min(defaultHeaders.length, headerRow.length); i++) {
                    headers.add(defaultHeaders[i]);
                    headerIndexMap.put(defaultHeaders[i], i);
                }
                log.info("기본 헤더 사용: {}", headers);
            }
            
            // 타입 추론 (첫 번째 데이터 행 사용)
            if (records.size() > startIndex) {
                String[] firstDataRow = records.get(startIndex);
                headerTypes = typeInferenceService.inferTypes(headers, firstDataRow);
                log.info("타입 추론 완료: {}", headerTypes);
            }
            
            // 데이터 파싱
            totalRows = records.size() - startIndex;
            
            for (int i = startIndex; i < records.size(); i++) {
                String[] record = records.get(i);
                log.debug("파싱 중인 레코드: {}", Arrays.toString(record));
                
                if (record.length >= headers.size()) {
                    try {
                        BankTransaction transaction = parseCsvRecordDynamic(record, headers, headerTypes);
                        transactions.add(transaction);
                        successfulRows++;
                    } catch (Exception e) {
                        log.warn("CSV 라인 {} 파싱 실패: {}", i + 1, e.getMessage());
                        failedRows++;
                    }
                } else {
                    log.warn("CSV 라인 {} 컬럼 수 부족: expected >= {}, actual = {}", 
                            i + 1, headers.size(), record.length);
                    failedRows++;
                }
            }
            
            if (transactions.isEmpty()) {
                throw new FileProcessingException(ErrorCode.CSV_PARSING_ERROR, "파싱 가능한 거래 데이터가 없습니다.");
            }
            
        } catch (IOException | CsvException e) {
            log.error("CSV 파일 파싱 중 오류 발생", e);
            throw new FileProcessingException(ErrorCode.CSV_PARSING_ERROR, "CSV 파일 파싱 중 오류가 발생했습니다.", e);
        }
        
        BankTransactionDataSet dataSet = BankTransactionDataSet.builder()
                .transactions(transactions)
                .headers(headers)
                .headerIndexMap(headerIndexMap)
                .fileName(csvFile.getOriginalFilename())
                .parsedAt(LocalDateTime.now())
                .totalRows(totalRows)
                .successfulRows(successfulRows)
                .failedRows(failedRows)
                .build();
        
        log.info("CSV 파싱 완료: {}", dataSet.getSummary());
        return dataSet;
    }
    
    private BankTransaction parseCsvRecordDynamic(String[] record, List<String> headers, Map<String, Class<?>> headerTypes) {
        Map<String, Object> attributes = new HashMap<>();
        
        for (int i = 0; i < headers.size() && i < record.length; i++) {
            String headerName = headers.get(i);
            String rawValue = record[i];
            Class<?> targetType = headerTypes.get(headerName);
            
            try {
                Object typedValue = typeInferenceService.parseWithType(rawValue, targetType);
                
                // LocalDateTime을 문자열로 변환하여 저장
                if (typedValue instanceof LocalDateTime) {
                    typedValue = formatDateTimeToString((LocalDateTime) typedValue);
                }
                
                attributes.put(headerName, typedValue);
            } catch (Exception e) {
                log.warn("컬럼 {} 파싱 실패: {} -> {}", headerName, rawValue, e.getMessage());
                attributes.put(headerName, rawValue); // 원본 문자열로 저장
            }
        }
        
        return BankTransaction.builder()
                .id(UUID.randomUUID())
                .attributes(attributes)
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    private String formatDateTimeToString(LocalDateTime dateTime) {
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private boolean detectHeader(String[] firstRow) {
        // 첫 번째 행에 한글 헤더나 영문 헤더가 포함되어 있는지 확인
        String firstCell = firstRow[0].toLowerCase().trim();
        return firstCell.contains("거래일시") || 
               firstCell.contains("transaction") || 
               firstCell.contains("date") ||
               firstCell.contains("적요") ||
               firstCell.contains("description");
    }
    
    
    public CompanyRulesDto parseJsonFile(MultipartFile jsonFile) {
        if (jsonFile == null || jsonFile.isEmpty()) {
            throw new FileProcessingException(ErrorCode.EMPTY_FILE, "JSON 파일이 비어있습니다.");
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            CompanyRulesDto rules = objectMapper.readValue(jsonFile.getInputStream(), CompanyRulesDto.class);
            
            if (rules.getCompanies() == null || rules.getCompanies().isEmpty()) {
                throw new FileProcessingException(ErrorCode.JSON_PARSING_ERROR, "JSON 파일에 회사 정보가 없습니다.");
            }
            
            log.info("JSON 파일에서 {}개 회사의 규칙 데이터 파싱 완료", rules.getCompanies().size());
            return rules;
            
        } catch (IOException e) {
            log.error("JSON 파일 파싱 중 오류 발생", e);
            throw new FileProcessingException(ErrorCode.JSON_PARSING_ERROR, "JSON 파일 파싱 중 오류가 발생했습니다.", e);
        }
    }
}