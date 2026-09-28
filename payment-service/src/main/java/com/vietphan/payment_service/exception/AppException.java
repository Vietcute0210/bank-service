package com.vietphan.payment_service.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private final Errors error;

    public AppException(Errors error) {
        super(error.getMessage());
        this.error = error;
    }
}
