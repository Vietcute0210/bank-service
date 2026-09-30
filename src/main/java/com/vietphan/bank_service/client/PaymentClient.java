package com.vietphan.bank_service.client;

import com.vietphan.bank_service.DTO.request.PaymentRequest;
import com.vietphan.bank_service.DTO.response.BaseResponse;
import com.vietphan.bank_service.DTO.response.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(
            RestClient.Builder restClientBuilder,
            @Value("${payment.service.url:http://localhost:8081}") String paymentServiceUrl
    ) {
        log.info("PaymentClient initialized with baseURL: {}", paymentServiceUrl);
        this.restClient = restClientBuilder
                .baseUrl(paymentServiceUrl)
                .build();
    }

    public BaseResponse<PaymentResponse> processPayment(PaymentRequest request) {
        log.info("Forwarding payment request to payment-service at /api/v1/payments: accountId={}, amount={}, currency={}",
                request.getAccountId(), request.getAmount(), request.getCurrency());
        try {
            return restClient.post()
                    .uri("/api/v1/payments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(new ParameterizedTypeReference<BaseResponse<PaymentResponse>>() {});
        } catch (RestClientException ex) {
            log.error("Failed to communicate with payment-service: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    public BaseResponse<PaymentResponse> getPaymentById(Long paymentId) {
        log.info("Fetching payment from payment-service with paymentId: {}", paymentId);
        return restClient.get()
                .uri("/api/v1/payments/{paymentId}", paymentId)
                .retrieve()
                .body(new ParameterizedTypeReference<BaseResponse<PaymentResponse>>() {});
    }

    public BaseResponse<List<PaymentResponse>> getPaymentsByAccountId(Long accountId) {
        log.info("Fetching payments from payment-service for accountId: {}", accountId);
        return restClient.get()
                .uri("/api/v1/payments/account/{accountId}", accountId)
                .retrieve()
                .body(new ParameterizedTypeReference<BaseResponse<List<PaymentResponse>>>() {});
    }
}
