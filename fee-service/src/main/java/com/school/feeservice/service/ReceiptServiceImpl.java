package com.school.feeservice.service;

import com.school.feeservice.client.StudentClient;
import com.school.feeservice.dto.ReceiptRequestDTO;
import com.school.feeservice.dto.ReceiptResponseDTO;
import com.school.feeservice.dto.StudentClientResponse;
import com.school.feeservice.entity.Receipt;
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
     * Collects student fee and generates a receipt.
     * Steps:
     * 1. Calls Student Service via Feign to validate student.
     * 2. If student exists, creates and saves a receipt with paymentStatus=SUCCESS.
     * 3. If Student Service fails, fallback method saves it as PENDING.
     *
     * @param request the fee collection details (studentId, amount, paymentMode, etc.)
     * @return ReceiptResponseDTO containing saved receipt details
     */
    @Override
    @CircuitBreaker(name = "studentServiceCB", fallbackMethod = "handleStudentServiceFailure")
    @Retry(name = "studentServiceCB")
    public ReceiptResponseDTO processFeePayment(ReceiptRequestDTO request) {
        log.info("Collecting fee for studentId={}, amount={}, mode={}", request.getStudentId(), request.getAmount(), request.getPaymentMode());

        StudentClientResponse student;
        try {
            student = studentClient.getByStudentId(request.getStudentId());
        } catch (Exception ex) {
            log.error("Student service call failed for {} : {}", request.getStudentId(), ex.getMessage());
            throw new StudentNotFoundException("Student not found: " + request.getStudentId());
        }

        if (student == null || student.getStudentId() == null) {
            log.warn("Student not found for id {}", request.getStudentId());
            throw new StudentNotFoundException("Student not found: " + request.getStudentId());
        }

        Receipt entity = ReceiptMapper.toEntity(request);
        entity.setPaymentStatus("SUCCESS");
        Receipt saved = repository.save(entity);

        log.info("Receipt saved id={}, receiptNumber={}, status={}", saved.getId(), saved.getReceiptNumber(), saved.getPaymentStatus());

        ReceiptResponseDTO response = ReceiptMapper.toDto(saved);
        response.setStudentName(student.getName());
        response.setGrade(student.getGrade());
        response.setSchoolName(student.getSchoolName());
        response.setCardNumber(maskCardNumber(request.getCardNumber()));

        return response;
    }
    /**
     * Fallback method triggered when Student Service is unavailable.
     * Saves the receipt as PENDING, masks sensitive data,
     * and returns a safe fallback response.
     *
     * @param request the original fee collection request
     * @param ex      the exception from the failed Student Service call
     * @return fallback ReceiptResponseDTO marked as PENDING
     */
    private ReceiptResponseDTO handleStudentServiceFailure(ReceiptRequestDTO request, Throwable ex) {
        log.error("Fallback triggered - Student service unavailable for studentId={} : {}", request.getStudentId(), ex.getMessage());

        //  Build entity with fallback status
        Receipt entity = ReceiptMapper.toEntity(request);
        entity.setPaymentStatus("PENDING");

        //  Save pending receipt
        Receipt saved = repository.save(entity);

        //  fallback response
        ReceiptResponseDTO response = ReceiptMapper.toDto(saved);
        response.setStudentName("N/A");
        response.setGrade("N/A");
        response.setSchoolName("N/A");
        response.setRemarks("Student service unavailable, stored as pending");

        // Mask card number if payment mode is CARD
        if ("CARD".equalsIgnoreCase(request.getPaymentMode()) && request.getCardNumber() != null) {
            response.setCardNumber(ReceiptMapper.maskCardNumber(request.getCardNumber()));
        } else {
            response.setCardNumber(null);
        }

        return response;
    }


    /**
     * Fetches a single receipt by its unique ID.
     * Throws ReceiptNotFoundException if not found.
     *
     * @param id receipt database ID
     * @return the corresponding ReceiptResponseDTO
     */
    @Override
    public ReceiptResponseDTO getReceipt(Long id) {
        log.info("Fetching receipt by id={}", id);
        Receipt receipt = repository.findById(id).orElseThrow(() -> new ReceiptNotFoundException("Receipt not found: " + id));
        log.debug("Receipt found for id={}", id);
        return ReceiptMapper.toDto(receipt);
    }

    /**
     * Retrieves all receipts associated with a specific student.
     *
     * @param studentId student’s business ID (e.g., S-ABC123)
     * @return list of ReceiptResponseDTO for that student
     */
    @Override
    public List<ReceiptResponseDTO> getReceiptsByStudent(String studentId) {
        log.info("Fetching all receipts for studentId={}", studentId);
        List<ReceiptResponseDTO> receipts = repository.findByStudentId(studentId).stream().map(ReceiptMapper::toDto).collect(Collectors.toList());
        log.debug("Found {} receipts for studentId={}", receipts.size(), studentId);
        return receipts;
    }
}
