package com.school.feeservice.service;

import com.school.feeservice.client.StudentClient;
import com.school.feeservice.dto.ReceiptRequestDTO;
import com.school.feeservice.dto.ReceiptResponseDTO;
import com.school.feeservice.dto.StudentClientResponse;
import com.school.feeservice.entity.Receipt;
import com.school.feeservice.exception.DuplicatePaymentException;
import com.school.feeservice.exception.ReceiptNotFoundException;
import com.school.feeservice.exception.StudentNotFoundException;
import com.school.feeservice.repository.ReceiptRepository;
import com.school.feeservice.util.ReceiptMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.school.feeservice.util.ReceiptMapper.maskCardNumber;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository repository;
    private final StudentClient studentClient;

    /**
     * Handles fee collection workflow:
     * 1. Validates duplicate payment
     * 2. Fetches student info (wrapped with CircuitBreaker)
     * 3. Saves successful receipt
     */
    @Override
    public ReceiptResponseDTO processFeePayment(ReceiptRequestDTO request) {
        log.info("Processing fee for studentId={}, amount={}, mode={}",
                request.getStudentId(), request.getAmount(), request.getPaymentMode());

        //   Duplicate payment validation (no CB involvement)
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime end = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59);

        boolean alreadyPaid = repository.existsByStudentIdAndPaymentDateBetween(request.getStudentId(), start, end);
        if (alreadyPaid) {
            log.warn("Duplicate payment detected for studentId={}", request.getStudentId());
            throw new DuplicatePaymentException("Fees for this month already paid for student: " + request.getStudentId());
        }

        //  Fetch student info (wrapped by CB)
        StudentClientResponse student = getStudentDetailsWithResilience(request.getStudentId());

        //  Save successful payment
        Receipt entity = ReceiptMapper.toEntity(request);
        entity.setPaymentStatus("SUCCESS");
        Receipt saved = repository.save(entity);

        log.info("Receipt saved id={}, receiptNumber={}, status={}",
                saved.getId(), saved.getReceiptNumber(), saved.getPaymentStatus());

        //  Prepare response
        ReceiptResponseDTO response = ReceiptMapper.toDto(saved);
        response.setStudentName(student.getName());
        response.setGrade(student.getGrade());
        response.setSchoolName(student.getSchoolName());
        response.setCardNumber(maskCardNumber(request.getCardNumber()));

        return response;
    }

    /**
     * Feign call wrapped with CircuitBreaker + Retry.
     * Only network failures will trigger fallback.
     */
    @CircuitBreaker(name = "studentServiceCB", fallbackMethod = "handleStudentServiceFailure")
    @Retry(name = "studentServiceCB")
    private StudentClientResponse getStudentDetailsWithResilience(String studentId) {
        log.info("Calling Student Service for studentId={}", studentId);
        StudentClientResponse student = studentClient.getByStudentId(studentId);
        if (student == null || student.getStudentId() == null) {
            throw new StudentNotFoundException("Student not found: " + studentId);
        }
        return student;
    }

    /**
     * Fallback when Student Service is down/unreachable.
     */
    private StudentClientResponse handleStudentServiceFailure(String studentId, Throwable ex) {
        log.error("Fallback triggered - Student service unavailable for studentId={} : {}", studentId, ex.getMessage());
        StudentClientResponse fallback = new StudentClientResponse();
        fallback.setStudentId(studentId);
        fallback.setName("N/A");
        fallback.setGrade("N/A");
        fallback.setSchoolName("N/A");
        return fallback;
    }

    /**
     * Get receipt by ID
     */
    @Override
    public ReceiptResponseDTO getReceipt(Long id) {
        log.info("Fetching receipt by id={}", id);
        Receipt receipt = repository.findById(id)
                .orElseThrow(() -> new ReceiptNotFoundException("Receipt not found: " + id));
        return ReceiptMapper.toDto(receipt);
    }

    /**
     * Get all receipts by studentId
     */
    @Override
    public List<ReceiptResponseDTO> getReceiptsByStudent(String studentId) {
        log.info("Fetching all receipts for studentId={}", studentId);
        return repository.findByStudentId(studentId)
                .stream()
                .map(ReceiptMapper::toDto)
                .collect(Collectors.toList());
    }
}
