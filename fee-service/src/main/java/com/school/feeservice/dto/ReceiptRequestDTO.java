package com.school.feeservice.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptRequestDTO {
    private String studentId;
    private Double amount;
    private String paymentMode;
    private String remarks;
    private String paymentStatus;
    private String cardNumber;
}
