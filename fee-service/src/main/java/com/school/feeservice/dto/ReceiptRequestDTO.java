package com.school.feeservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptRequestDTO {

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private Double amount;

    @NotBlank(message = "Payment mode is required")
    @Pattern(regexp = "^(CARD|UPI|CASH)$", message = "Payment mode must be CARD, UPI, or CASH")
    private String paymentMode;

    private String remarks;

    @Pattern(regexp = "^(SUCCESS|PENDING|FAILED)$", message = "Invalid payment status")
    private String paymentStatus;

    // Conditional field: required only if paymentMode == CARD
    @Size(min = 8, max = 16, message = "Card number must be between 8 and 16 digits")
    private String cardNumber;
}
