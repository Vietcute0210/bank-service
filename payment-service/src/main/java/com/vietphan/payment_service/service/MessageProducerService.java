package com.vietphan.payment_service.service;

import com.vietphan.payment_service.DTO.message.PaymentMessage;

public interface MessageProducerService {

    void sendPaymentCompleted(PaymentMessage message);
}
