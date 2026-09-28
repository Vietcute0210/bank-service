package com.vietphan.payment_service.controller;

import com.vietphan.payment_service.DTO.request.PaymentRequest;
import com.vietphan.payment_service.DTO.response.BaseResponse;
import com.vietphan.payment_service.DTO.response.PaymentResponse;
import com.vietphan.payment_service.enums.PaymentStatus;
import com.vietphan.payment_service.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = PaymentResponse.builder()
                .paymentId(100L)
                .accountId(1L)
                .amount(500000.0)
                .currency("VND")
                .status(PaymentStatus.SUCCESS)
                .description("Test payment")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/payments - process payment successfully")
    void testProcessPayment() {
        PaymentRequest request = PaymentRequest.builder()
                .accountId(1L)
                .amount(500000.0)
                .currency("VND")
                .description("Test payment")
                .build();

        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(sampleResponse);

        BaseResponse<PaymentResponse> response = paymentController.processPayment(request);

        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertEquals("Payment processed successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(100L, response.getData().getPaymentId());

        verify(paymentService, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/payments/{paymentId} - get payment by ID successfully")
    void testGetPaymentById() {
        when(paymentService.getPaymentById(100L)).thenReturn(sampleResponse);

        BaseResponse<PaymentResponse> response = paymentController.getPaymentById(100L);

        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertEquals("Get payment successfully", response.getMessage());
        assertEquals(100L, response.getData().getPaymentId());

        verify(paymentService, times(1)).getPaymentById(100L);
    }

    @Test
    @DisplayName("GET /api/v1/payments/account/{accountId} - get payments by account ID successfully")
    void testGetPaymentsByAccountId() {
        when(paymentService.getPaymentsByAccountId(1L)).thenReturn(List.of(sampleResponse));

        BaseResponse<List<PaymentResponse>> response = paymentController.getPaymentsByAccountId(1L);

        assertNotNull(response);
        assertEquals(1000, response.getCode());
        assertEquals("Get account payments successfully", response.getMessage());
        assertEquals(1, response.getData().size());
        assertEquals(100L, response.getData().getFirst().getPaymentId());

        verify(paymentService, times(1)).getPaymentsByAccountId(1L);
    }
}
