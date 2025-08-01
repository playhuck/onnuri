package com.side.onnuri.common.exception;

public class TransactionProcessingException extends BusinessException {
    
    public TransactionProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public TransactionProcessingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public TransactionProcessingException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}