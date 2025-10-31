package com.school.feeservice.service;

import com.school.feeservice.client.StudentClient;
import com.school.feeservice.dto.*;
import com.school.feeservice.entity.Receipt;
import com.school.feeservice.exception.ReceiptNotFoundException;
import com.school.feeservice.exception.StudentNotFoundException;
import com.school.feeservice.repository.ReceiptRepository;
import com.school.feeservice.util.ReceiptMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReceiptServiceImplTest {

    @Mock
    private ReceiptRepository repository;

    @Mock
    private StudentClient studentClient;

    @InjectMocks
    private ReceiptServiceImpl service;

    private ReceiptRequestDTO request;
    private StudentClientResponse student;
    private Receipt savedReceipt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = ReceiptRequestDTO.builder()
                .studentId("S-12345")
                .amount(5000.0)
                .paymentMode("CREDIT_CARD")
                .cardNumber("5123456745")
                .remarks("Initial test")
                .build();

        student = StudentClientResponse.builder()
                .studentId("S-12345")
                .name("John Doe")
                .grade("10")
                .schoolName("Springfield High")
                .build();

        savedReceipt = Receipt.builder()
                .id(1L)
                .receiptNumber("REC-1001")
                .studentId("S-12345")
                .amount(5000.0)
                .paymentMode("CREDIT_CARD")
                .paymentStatus("SUCCESS")
                .remarks("Initial test")
                .build();
    }

    @Test
    void testProcessFeePayment_Success() {
        // Arrange
        when(studentClient.getByStudentId(request.getStudentId())).thenReturn(student);
        when(repository.save(any(Receipt.class))).thenReturn(savedReceipt);

        // Act
        ReceiptResponseDTO response = service.processFeePayment(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStudentName()).isEqualTo("John Doe");
        assertThat(response.getPaymentStatus()).isEqualTo("SUCCESS");
        verify(repository, times(1)).save(any(Receipt.class));
        verify(studentClient, times(1)).getByStudentId(request.getStudentId());
    }

    @Test
    void testProcessFeePayment_StudentNotFound() {
        when(studentClient.getByStudentId(anyString())).thenReturn(null);

        assertThatThrownBy(() -> service.processFeePayment(request))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void testProcessFeePayment_ThrowsExceptionFromClient() {
        when(studentClient.getByStudentId(anyString())).thenThrow(new RuntimeException("Service Down"));

        assertThatThrownBy(() -> service.processFeePayment(request))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    void testHandleStudentServiceFailure_Fallback() throws Exception {
        // Using reflection to call private fallback
        java.lang.reflect.Method fallbackMethod = ReceiptServiceImpl.class.getDeclaredMethod(
                "handleStudentServiceFailure", ReceiptRequestDTO.class, Throwable.class);
        fallbackMethod.setAccessible(true);

        ArgumentCaptor<Receipt> captor = ArgumentCaptor.forClass(Receipt.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        ReceiptResponseDTO response = (ReceiptResponseDTO)
                fallbackMethod.invoke(service, request, new RuntimeException("Timeout"));

        Receipt savedEntity = captor.getValue();

        assertThat(savedEntity.getPaymentStatus()).isEqualTo("PENDING");
        assertThat(response).isNotNull();
        assertThat(response.getPaymentStatus()).isEqualTo("PENDING");
        assertThat(response.getRemarks()).contains("Student service unavailable");
    }


    @Test
    void testGetReceipt_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(savedReceipt));

        ReceiptResponseDTO response = service.getReceipt(1L);

        assertThat(response).isNotNull();
        assertThat(response.getReceiptNumber()).isEqualTo("REC-1001");
    }

    @Test
    void testGetReceipt_NotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getReceipt(10L))
                .isInstanceOf(ReceiptNotFoundException.class)
                .hasMessageContaining("Receipt not found");
    }

    @Test
    void testGetReceiptsByStudent_ReturnsList() {
        when(repository.findByStudentId("S-12345")).thenReturn(List.of(savedReceipt));

        List<ReceiptResponseDTO> result = service.getReceiptsByStudent("S-12345");

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getStudentId()).isEqualTo("S-12345");
        verify(repository, times(1)).findByStudentId("S-12345");
    }
}
