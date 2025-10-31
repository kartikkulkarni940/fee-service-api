package com.school.feeservice.service;

import com.school.feeservice.dto.*;
import java.util.List;

public interface ReceiptService {
    ReceiptResponseDTO processFeePayment(ReceiptRequestDTO request);
    ReceiptResponseDTO getReceipt(Long id);
    List<ReceiptResponseDTO> getReceiptsByStudent(String studentId);
}
