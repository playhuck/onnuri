package com.side.onnuri.common.exception;

public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public EntityNotFoundException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
    
    public EntityNotFoundException(String entityName, Object identifier) {
        super(ErrorCode.ENTITY_NOT_FOUND, String.format("%s를 찾을 수 없습니다. ID: %s", entityName, identifier));
    }
}