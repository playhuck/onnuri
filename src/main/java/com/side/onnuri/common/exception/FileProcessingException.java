package com.side.onnuri.common.exception;

public class FileProcessingException extends BusinessException {
    
    public FileProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public FileProcessingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public FileProcessingException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}