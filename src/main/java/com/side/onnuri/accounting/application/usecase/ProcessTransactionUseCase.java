package com.side.onnuri.accounting.application.usecase;

import com.side.onnuri.accounting.application.dto.ProcessResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProcessTransactionUseCase {
    ProcessResponse execute(MultipartFile bankTransactionsCsv, MultipartFile rulesJson);
}