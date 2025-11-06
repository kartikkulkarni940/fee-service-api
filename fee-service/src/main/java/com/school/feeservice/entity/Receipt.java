package com.school.feeservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.YearMonth;

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

    @Column(nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(nullable = false, length = 50)
    private String studentId;

    private String remarks;
    private String studentName;
    private String grade;
    private String schoolName;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 20, nullable = false)
    private String paymentMode;

    @Column(length = 20, nullable = false)
    private String paymentStatus;

    private LocalDateTime paymentDate;

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
