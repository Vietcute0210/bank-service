package com.vietphan.payment_service.service;

import com.vietphan.payment_service.DTO.message.PaymentMessage;
import com.vietphan.payment_service.DTO.request.PaymentRequest;
import com.vietphan.payment_service.DTO.response.PaymentResponse;
import com.vietphan.payment_service.entity.Payment;
import com.vietphan.payment_service.enums.PaymentStatus;
import com.vietphan.payment_service.exception.AppException;
import com.vietphan.payment_service.exception.Errors;
import com.vietphan.payment_service.mapper.PaymentMapper;
import com.vietphan.payment_service.repository.PaymentRepository;
import com.vietphan.payment_service.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Spy
    private PaymentMapper paymentMapper;

    @Mock
    private MessageProducerService messageProducerService;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest validRequest;
    private Payment savedPayment;

    @BeforeEach
    void setUp() {
        validRequest = PaymentRequest.builder()
                .accountId(1L)
                .amount(500000.0)
                .currency("VND")
                .description("Thanh toán tiền điện")
                .build();

        savedPayment = Payment.builder()
                .paymentId(100L)
                .accountId(1L)
                .amount(500000.0)
                .currency("VND")
                .status(PaymentStatus.SUCCESS)
                .description("Thanh toán tiền điện")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    @DisplayName("Process payment successfully - saves payment and sends message to queue")
    void testProcessPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);
        doNothing().when(messageProducerService).sendPaymentCompleted(any(PaymentMessage.class));

        PaymentResponse response = paymentService.processPayment(validRequest);

        assertNotNull(response);
        assertEquals(100L, response.getPaymentId());
        assertEquals(1L, response.getAccountId());
        assertEquals(500000.0, response.getAmount());
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(messageProducerService, times(1)).sendPaymentCompleted(any(PaymentMessage.class));
    }

    @Test
    @DisplayName("Process payment fails when accountId is null")
    void testProcessPayment_NullAccountId() {
        validRequest.setAccountId(null);

        AppException ex = assertThrows(AppException.class, () -> paymentService.processPayment(validRequest));
        assertEquals(Errors.INVALID_ACCOUNT_ID, ex.getError());

        verify(paymentRepository, never()).save(any());
        verify(messageProducerService, never()).sendPaymentCompleted(any());
    }

    @Test
    @DisplayName("Process payment fails when amount is zero or negative")
    void testProcessPayment_InvalidAmount() {
        validRequest.setAmount(0);

        AppException ex = assertThrows(AppException.class, () -> paymentService.processPayment(validRequest));
        assertEquals(Errors.INVALID_PAYMENT_AMOUNT, ex.getError());

        validRequest.setAmount(-100);
        AppException exNegative = assertThrows(AppException.class, () -> paymentService.processPayment(validRequest));
        assertEquals(Errors.INVALID_PAYMENT_AMOUNT, exNegative.getError());

        verify(paymentRepository, never()).save(any());
        verify(messageProducerService, never()).sendPaymentCompleted(any());
    }

    @Test
    @DisplayName("Get payment by id - found")
    void testGetPaymentById_Success() {
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(savedPayment));

        PaymentResponse response = paymentService.getPaymentById(100L);

        assertNotNull(response);
        assertEquals(100L, response.getPaymentId());
        assertEquals(1L, response.getAccountId());
        verify(paymentRepository, times(1)).findById(100L);
    }

    @Test
    @DisplayName("Get payment by id - not found throws PAYMENT_NOT_FOUND")
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> paymentService.getPaymentById(999L));
        assertEquals(Errors.PAYMENT_NOT_FOUND, ex.getError());
    }

    @Test
    @DisplayName("Get payments by account id - success")
    void testGetPaymentsByAccountId_Success() {
        when(paymentRepository.findByAccountIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(savedPayment));

        List<PaymentResponse> responses = paymentService.getPaymentsByAccountId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(100L, responses.getFirst().getPaymentId());
        verify(paymentRepository, times(1)).findByAccountIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Get payments by account id - null account id throws INVALID_ACCOUNT_ID")
    void testGetPaymentsByAccountId_NullAccountId() {
        AppException ex = assertThrows(AppException.class, () -> paymentService.getPaymentsByAccountId(null));
        assertEquals(Errors.INVALID_ACCOUNT_ID, ex.getError());
    }
}
