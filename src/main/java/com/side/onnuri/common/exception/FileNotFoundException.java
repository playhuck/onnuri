package com.side.onnuri.common.exception;

public class FileNotFoundException extends BusinessException {
    
    public FileNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public FileNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public FileNotFoundException(String fileName) {
        super(ErrorCode.FILE_PROCESSING_ERROR, String.format("필요한 파일이 없습니다: %s", fileName));
    }
}