package com.vietphan.bank_service.exception;

import com.vietphan.bank_service.DTO.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

@RestControllerAdvice
public class HandleGlobalException {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<BaseResponse<Void>> handleAppException(AppException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .code(ex.getError().getCode())
                .message(ex.getError().getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = Errors.INVALID_ID_FORMAT.getMessage();
        if (ex.getRequiredType() != null && !ex.getRequiredType().equals(UUID.class)) {
            message = String.format("Invalid parameter '%s': Expected type %s", ex.getName(), ex.getRequiredType().getSimpleName());
        }

        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .code(Errors.INVALID_ID_FORMAT.getCode())
                .message(message)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .code(Errors.INVALID_ID_FORMAT.getCode())
                .message("Invalid parameter format: " + ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneralException(Exception ex) {
        BaseResponse<Void> response = BaseResponse.<Void>builder()
                .code(9999)
                .message(ex.getMessage() != null ? ex.getMessage() : "Internal server error")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
