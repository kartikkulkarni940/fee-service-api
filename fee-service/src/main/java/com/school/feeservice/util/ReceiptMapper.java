package com.school.feeservice.util;

import com.school.feeservice.dto.ReceiptRequestDTO;
import com.school.feeservice.dto.ReceiptResponseDTO;
import com.school.feeservice.entity.Receipt;

public class ReceiptMapper {

    /**
     * Converts DTO → Entity.
     * Always masks card number before saving (to ensure PCI compliance).
     */
    public static Receipt toEntity(ReceiptRequestDTO dto) {
        if (dto == null) return new Receipt();

        // Mask card number before persisting (DB never stores real PAN)
        String maskedCard = null;
        if ("CARD".equalsIgnoreCase(dto.getPaymentMode()) && dto.getCardNumber() != null) {
            maskedCard = maskCardNumber(dto.getCardNumber());
        }

        return Receipt.builder()
                .studentId(dto.getStudentId())
                .amount(dto.getAmount())
                .paymentMode(dto.getPaymentMode())
                .paymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "PENDING")
                .remarks(dto.getRemarks())
                .cardNumber(maskedCard) // ✅ only masked version stored
                .build();
    }

    /**
     * Converts Entity → DTO (Response).
     * Returns masked card number safely.
     */
    public static ReceiptResponseDTO toDto(Receipt entity) {
        if (entity == null) return new ReceiptResponseDTO();

        return ReceiptResponseDTO.builder()
                .id(entity.getId())
                .receiptNumber(entity.getReceiptNumber())
                .studentId(entity.getStudentId())
                .studentName(entity.getStudentName())
                .grade(entity.getGrade())
                .schoolName(entity.getSchoolName())
                .amount(entity.getAmount())
                .paymentMode(entity.getPaymentMode())
                .paymentStatus(entity.getPaymentStatus())
                .remarks(entity.getRemarks())
                .paymentDate(entity.getPaymentDate())
                .cardNumber(entity.getCardNumber()) // ✅ already masked in DB
                .build();
    }

    /**
     * Masks card number.
     * Example: 1234567812345678 → 12****78
     */
    public static String maskCardNumber(String input) {
        if (input == null || input.length() <= 4) return "****";
        return input.substring(0, 2) + "****" + input.substring(input.length() - 2);
    }

    /**
     * Masks mobile number.
     * Example: 9876543210 → 987*****10
     */
    public static String maskMobile(String mobile) {
        if (mobile == null || mobile.length() < 4) return "****";
        return mobile.substring(0, 3) + "*****" + mobile.substring(mobile.length() - 2);
    }
}
