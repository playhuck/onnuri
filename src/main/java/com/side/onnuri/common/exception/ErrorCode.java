package com.side.onnuri.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "잘못된 입력값입니다."),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_003", "잘못된 타입입니다."),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_004", "엔티티를 찾을 수 없습니다."),
    
    // File Processing
    FILE_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "FILE_001", "파일 처리 중 오류가 발생했습니다."),
    CSV_PARSING_ERROR(HttpStatus.BAD_REQUEST, "FILE_002", "CSV 파일 파싱 중 오류가 발생했습니다."),
    JSON_PARSING_ERROR(HttpStatus.BAD_REQUEST, "FILE_003", "JSON 파일 파싱 중 오류가 발생했습니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "FILE_004", "지원하지 않는 파일 형식입니다."),
    EMPTY_FILE(HttpStatus.BAD_REQUEST, "FILE_005", "빈 파일입니다."),
    REQUIRED_FILE_MISSING(HttpStatus.BAD_REQUEST, "FILE_006", "필수 파일이 누락되었습니다."),
    
    // Transaction Processing
    TRANSACTION_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TRANSACTION_001", "거래 처리 중 오류가 발생했습니다."),
    CLASSIFICATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "TRANSACTION_002", "거래 분류 중 오류가 발생했습니다."),
    DUPLICATE_TRANSACTION(HttpStatus.CONFLICT, "TRANSACTION_003", "중복된 거래입니다."),
    
    // Company & Category
    COMPANY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_001", "회사를 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMPANY_002", "카테고리를 찾을 수 없습니다."),
    INVALID_COMPANY_ID(HttpStatus.BAD_REQUEST, "COMPANY_003", "잘못된 회사 ID입니다."),
    
    // Database
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DB_001", "데이터베이스 오류가 발생했습니다."),
    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "DB_002", "데이터 무결성 제약 조건을 위반했습니다.");
    
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}