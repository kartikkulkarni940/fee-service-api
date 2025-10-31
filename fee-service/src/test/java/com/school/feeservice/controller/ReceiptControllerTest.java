package com.school.feeservice.controller;

import com.school.feeservice.dto.ReceiptRequestDTO;
import com.school.feeservice.dto.ReceiptResponseDTO;
import com.school.feeservice.service.ReceiptService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for ReceiptController.
 * Uses MockMvc to simulate HTTP requests and verify JSON responses.
 */
@WebMvcTest(ReceiptController.class)
class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReceiptService receiptService;

    @Test
    void testCollectFee() throws Exception {
        ReceiptResponseDTO response = ReceiptResponseDTO.builder()
                .id(1L)
                .receiptNumber("R-001")
                .studentId("S-001")
                .studentName("Ravi Kumar")
                .grade("10")
                .schoolName("Delhi Public School")
                .amount(5000.0)
                .paymentMode("CARD")
                .cardNumber("XXXX-XXXX-XXXX-4321") //  optional field
                .paymentStatus("PAID")
                .remarks("Fee collected successfully")
                .paymentDate(LocalDateTime.now())
                .build();

        when(receiptService.processFeePayment(any(ReceiptRequestDTO.class))).thenReturn(response);

        String json = """
                {
                    "studentId": "S-001",
                    "amount": 5000.0,
                    "paymentMode": "CARD",
                    "cardNumber": "1234-5678-9876-4321"
                }
                """;

        mockMvc.perform(post("/api/receipts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentId").value("S-001"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.cardNumber").value("XXXX-XXXX-XXXX-4321"));
    }

    @Test
    void testGetReceipt() throws Exception {
        ReceiptResponseDTO response = ReceiptResponseDTO.builder()
                .id(1L)
                .receiptNumber("R-001")
                .studentId("S-001")
                .studentName("Ravi Kumar")
                .grade("10")
                .schoolName("Delhi Public School")
                .amount(5000.0)
                .paymentMode("UPI")
                .paymentStatus("PAID")
                .remarks("Fee collected successfully")
                .paymentDate(LocalDateTime.now())
                .build();

        when(receiptService.getReceipt(1L)).thenReturn(response);

        mockMvc.perform(get("/api/receipts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value("S-001"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    void testGetReceiptsByStudent() throws Exception {
        List<ReceiptResponseDTO> responses = List.of(
                ReceiptResponseDTO.builder()
                        .id(1L)
                        .receiptNumber("R-001")
                        .studentId("S-001")
                        .studentName("Ravi Kumar")
                        .grade("10")
                        .schoolName("Delhi Public School")
                        .amount(5000.0)
                        .paymentMode("UPI")
                        .paymentStatus("PAID")
                        .remarks("First Term")
                        .paymentDate(LocalDateTime.now())
                        .build(),
                ReceiptResponseDTO.builder()
                        .id(2L)
                        .receiptNumber("R-002")
                        .studentId("S-001")
                        .studentName("Ravi Kumar")
                        .grade("10")
                        .schoolName("Delhi Public School")
                        .amount(3000.0)
                        .paymentMode("CARD")
                        .cardNumber("XXXX-XXXX-XXXX-1111")
                        .paymentStatus("PAID")
                        .remarks("Second Term")
                        .paymentDate(LocalDateTime.now())
                        .build()
        );

        when(receiptService.getReceiptsByStudent("S-001")).thenReturn(responses);

        mockMvc.perform(get("/api/receipts/student/S-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].paymentMode").value("UPI"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].cardNumber").value("XXXX-XXXX-XXXX-1111"));
    }
}
