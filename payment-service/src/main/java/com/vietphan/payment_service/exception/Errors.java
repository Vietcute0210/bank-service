package com.vietphan.payment_service.exception;

public enum Errors {
    PAYMENT_NOT_FOUND(2001, "Payment not found"),
    INVALID_PAYMENT_AMOUNT(2002, "Payment amount must be greater than 0"),
    INVALID_ACCOUNT_ID(2003, "Account ID is required"),
    PAYMENT_FAILED(2004, "Payment processing failed"),
    MESSAGE_QUEUE_ERROR(2005, "Failed to send message to message queue"),
    INTERNAL_SERVER_ERROR(9999, "Internal server error");

    private final int code;
    private final String message;

    Errors(int code, String message) {
        this.code = code;
        this.message = message;
    }

    Errors(String message) {
        this(9999, message);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
