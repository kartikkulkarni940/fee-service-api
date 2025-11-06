package com.school.feeservice.repository;

import com.school.feeservice.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByStudentId(String studentId);

    boolean existsByStudentIdAndPaymentDateBetween(String studentId, LocalDateTime start, LocalDateTime end);

}
