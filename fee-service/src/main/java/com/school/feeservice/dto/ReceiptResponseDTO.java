package com.school.feeservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptResponseDTO {
    private Long id;
    private String receiptNumber;
    private String studentId;
    private String studentName;
    private String grade;
    private String schoolName;
    private Double amount;
    private String paymentMode;
    private String paymentStatus;
    private String remarks;
    private LocalDateTime paymentDate;
    private String cardNumber;
}
