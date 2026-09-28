package com.vietphan.payment_service.service.impl;

import com.vietphan.payment_service.DTO.message.PaymentMessage;
import com.vietphan.payment_service.DTO.request.PaymentRequest;
import com.vietphan.payment_service.DTO.response.PaymentResponse;
import com.vietphan.payment_service.entity.Payment;
import com.vietphan.payment_service.enums.PaymentStatus;
import com.vietphan.payment_service.exception.AppException;
import com.vietphan.payment_service.exception.Errors;
import com.vietphan.payment_service.mapper.PaymentMapper;
import com.vietphan.payment_service.repository.PaymentRepository;
import com.vietphan.payment_service.service.MessageProducerService;
import com.vietphan.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final MessageProducerService messageProducerService;

    @Override
    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment request for accountId: {}, amount: {}", request.getAccountId(), request.getAmount());

        if (request.getAccountId() == null) {
            throw new AppException(Errors.INVALID_ACCOUNT_ID);
        }

        if (request.getAmount() <= 0) {
            throw new AppException(Errors.INVALID_PAYMENT_AMOUNT);
        }

        Payment payment = paymentMapper.toEntity(request);
        payment.setStatus(PaymentStatus.SUCCESS);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved successfully with paymentId: {}", savedPayment.getPaymentId());

        PaymentMessage message = paymentMapper.toMessage(savedPayment);
        messageProducerService.sendPaymentCompleted(message);

        return paymentMapper.toResponse(savedPayment);
    }

    @Override
    public PaymentResponse getPaymentById(Long paymentId) {
        if (paymentId == null) {
            throw new AppException(Errors.PAYMENT_NOT_FOUND);
        }

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new AppException(Errors.PAYMENT_NOT_FOUND));

        return paymentMapper.toResponse(payment);
    }

    @Override
    public List<PaymentResponse> getPaymentsByAccountId(Long accountId) {
        if (accountId == null) {
            throw new AppException(Errors.INVALID_ACCOUNT_ID);
        }

        List<Payment> payments = paymentRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
        return payments.stream()
                .map(paymentMapper::toResponse)
                .toList();
    }
}
