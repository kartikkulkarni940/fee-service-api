package com.school.feeservice.repository;

import com.school.feeservice.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    List<Receipt> findByStudentId(String studentId);
}
