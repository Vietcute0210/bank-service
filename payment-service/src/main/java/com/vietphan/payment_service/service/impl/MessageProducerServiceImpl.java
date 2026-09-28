package com.vietphan.payment_service.service.impl;

import com.vietphan.payment_service.DTO.message.PaymentMessage;
import com.vietphan.payment_service.constant.QueueConstants;
import com.vietphan.payment_service.exception.AppException;
import com.vietphan.payment_service.exception.Errors;
import com.vietphan.payment_service.service.MessageProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducerServiceImpl implements MessageProducerService {

    private final JmsTemplate jmsTemplate;

    @Value("${activemq.queue.payment-completed:" + QueueConstants.PAYMENT_COMPLETED_QUEUE + "}")
    private String paymentCompletedQueue;

    @Override
    public void sendPaymentCompleted(PaymentMessage message) {
        log.info("Sending payment completed message to queue [{}]: paymentId={}, accountId={}, amount={}",
                paymentCompletedQueue, message.getPaymentId(), message.getAccountId(), message.getAmount());
        try {
            jmsTemplate.convertAndSend(paymentCompletedQueue, message);
            log.info("Successfully sent message to queue [{}] for paymentId={}", paymentCompletedQueue, message.getPaymentId());
        } catch (Exception ex) {
            log.error("Failed to send message to queue [{}]: {}", paymentCompletedQueue, ex.getMessage(), ex);
            throw new AppException(Errors.MESSAGE_QUEUE_ERROR);
        }
    }
}
