package com.vietphan.bank_service.exception;

public enum Errors {
    ACCOUNT_NOT_FOUND(1001, "Account not found"),
    ACCOUNT_ALREADY_EXISTS(1002, "Account already exists"),
    ACCOUNT_INACTIVE(1003, "Account is inactive"),
    ACCOUNT_BLOCKED(1004, "Account is blocked"),
    ACCOUNT_CLOSED(1005, "Account is closed"),
    ACCOUNT_HAS_LINKED_CARDS(1006, "Cannot delete account: Account has linked cards"),
    ACCOUNT_HAS_NON_ZERO_BALANCE(1007, "Cannot delete account: Account balance must be zero"),
    INVALID_ID_FORMAT(1008, "Invalid ID format: Must be a valid UUID"),
    CARD_NOT_FOUND(1009, "Card not found"),
    CARD_ALREADY_EXISTS(1010, "Card already exists"),
    CARD_INACTIVE(1011, "Card is inactive"),
    CARD_BLOCKED(1012, "Card is blocked"),
    CARD_EXPIRED(1013, "Card is expired"),
    INSUFFICIENT_FUNDS(1014, "Insufficient funds"),
    INVALID_AMOUNT(1015, "Invalid amount"),
    INVALID_ACCOUNT_TYPE(1016, "Invalid account type"),
    INVALID_CARD_TYPE(1017, "Invalid card type"),
    CARD_HAS_PENDING_TRANSACTIONS(1018, "Cannot delete card: Card has pending transactions"),
    BALANCE_NOT_FOUND(1019, "Balance not found"),

    REFRESHTOKEN_NOT_FOUND(1020,"Refresh token does not exist"),
    REFRESHTOKEN_REVOKED(1021,"Refresh token was expired or revoked"),
    UNAUTHENTICATED(1022, "Full authentication is required to access this resource"),
    FORBIDDEN(1023, "Forbidden: You do not have permission to access this resource"),
    ACCESS_DENIED(1024, "Access denied");

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
