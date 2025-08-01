package com.side.onnuri.accounting.application.usecase;

import com.side.onnuri.accounting.application.dto.TransactionRecordResponse;

import java.util.List;

public interface GetTransactionRecordsUseCase {
    List<TransactionRecordResponse> execute(String companyId);
}