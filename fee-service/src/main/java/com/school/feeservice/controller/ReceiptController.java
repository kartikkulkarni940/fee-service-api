package com.school.feeservice.controller;

import com.school.feeservice.dto.*;
import com.school.feeservice.service.ReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
@Tag(name = "Fee Service", description = "Handles student fee collection and receipt management")
public class ReceiptController {

    private final ReceiptService service;

    @Operation(
            summary = "Collect student fee",
            description = "Accepts student fee payment and generates a receipt entry"
    )
    @PostMapping
    public ResponseEntity<ReceiptResponseDTO> collectFee(@RequestBody @Validated ReceiptRequestDTO request) {
        log.info("[POST] /api/receipts - Collect fee for studentId={}", request.getStudentId());
        return ResponseEntity.ok(service.processFeePayment(request));
    }

    @Operation(
            summary = "Fetch receipt by ID",
            description = "Retrieves a single fee receipt record using receipt ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ReceiptResponseDTO> getReceipt(@PathVariable Long id) {
        log.info("[GET] /api/receipts/{} - Fetch receipt by ID", id);
        return ResponseEntity.ok(service.getReceipt(id));
    }

    @Operation(
            summary = "List all receipts by student",
            description = "Returns all receipts for the given student ID"
    )
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByStudent(@PathVariable String studentId) {
        log.info("[GET] /api/receipts/student/{} - Fetch receipts for studentId={}", studentId, studentId);
        return ResponseEntity.ok(service.getReceiptsByStudent(studentId));
    }
}
