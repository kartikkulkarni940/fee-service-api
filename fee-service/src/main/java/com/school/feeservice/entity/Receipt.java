package com.school.feeservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String studentId;

    @Size(max = 255)
    private String remarks;

    @NotBlank
    private String studentName;

    @NotBlank
    private String grade;

    @NotBlank
    private String schoolName;

    @NotNull
    @Positive
    @Column(nullable = false)
    private Double amount;

    @NotBlank
    @Column(length = 20)
    private String paymentMode;  // CASH / UPI / CARD

    @NotBlank
    @Column(length = 20)
    private String paymentStatus; // SUCCESS / PENDING / FAILED

    private LocalDateTime paymentDate;

    /**
     * Stores only masked card number for CARD transactions.
     * Never store full PAN or CVV to stay PCI-DSS compliant.
     * Example: "12****78"
     */
    @Column(length = 30)
    private String cardNumber;

    @PrePersist
    public void prePersist() {
        this.paymentDate = LocalDateTime.now();
        if (this.receiptNumber == null || this.receiptNumber.isBlank()) {
            this.receiptNumber = "REC-" + System.currentTimeMillis();
        }
    }
}
