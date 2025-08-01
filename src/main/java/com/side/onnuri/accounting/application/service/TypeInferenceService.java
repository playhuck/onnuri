package com.side.onnuri.accounting.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class TypeInferenceService {
    
    private static final Pattern DATE_PATTERN = Pattern.compile(
        "\\d{4}[-/]\\d{2}[-/]\\d{2}(\\s\\d{2}:\\d{2}(:\\d{2})?)?"
    );
    
    private static final Pattern NUMBER_PATTERN = Pattern.compile(
        "^[0-9,]+(\\.\\d+)?$"
    );
    
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyyyMMdd")
    };
    
    public Map<String, Class<?>> inferTypes(List<String> headers, String[] firstDataRow) {
        Map<String, Class<?>> headerTypes = new HashMap<>();
        
        for (int i = 0; i < headers.size() && i < firstDataRow.length; i++) {
            String header = headers.get(i);
            String value = firstDataRow[i];
            Class<?> inferredType = inferType(value);
            headerTypes.put(header, inferredType);
            
            log.debug("타입 추론 결과 - {}: {} -> {}", header, value, inferredType.getSimpleName());
        }
        
        return headerTypes;
    }
    
    private Class<?> inferType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return String.class;
        }
        
        String trimmedValue = value.trim();
        
        // 날짜/시간 타입 확인
        if (isDateTime(trimmedValue)) {
            return LocalDateTime.class;
        }
        
        // 숫자 타입 확인
        if (isNumber(trimmedValue)) {
            return BigDecimal.class;
        }
        
        // 기본값: 문자열
        return String.class;
    }
    
    private boolean isDateTime(String value) {
        if (!DATE_PATTERN.matcher(value).matches()) {
            return false;
        }
        
        // 실제 파싱 가능한지 확인
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDateTime.parse(value, formatter);
                return true;
            } catch (DateTimeParseException e) {
                // 다음 포맷터 시도
            }
        }
        
        return false;
    }
    
    private boolean isNumber(String value) {
        if (!NUMBER_PATTERN.matcher(value).matches()) {
            return false;
        }
        
        try {
            String cleanValue = value.replaceAll(",", "");
            new BigDecimal(cleanValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public Object parseWithType(String rawValue, Class<?> targetType) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            if (targetType == BigDecimal.class) {
                return BigDecimal.ZERO;
            }
            return null;
        }
        
        String trimmedValue = rawValue.trim();
        
        if (targetType == LocalDateTime.class) {
            return parseDateTime(trimmedValue);
        } else if (targetType == BigDecimal.class) {
            return parseNumber(trimmedValue);
        } else {
            return trimmedValue;
        }
    }
    
    private LocalDateTime parseDateTime(String value) {
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // 다음 포맷터 시도
            }
        }
        
        throw new IllegalArgumentException("날짜 형식을 파싱할 수 없습니다: " + value);
    }
    
    private BigDecimal parseNumber(String value) {
        try {
            String cleanValue = value.replaceAll(",", "");
            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            log.warn("숫자 파싱 실패: {}, 0으로 처리", value);
            return BigDecimal.ZERO;
        }
    }
}