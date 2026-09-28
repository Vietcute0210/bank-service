package com.vietphan.payment_service.DTO.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {

    @Builder.Default
    private int code = 1000;

    private String message;

    private T data;

    public static <T> BaseResponse<T> ok(T data) {
        return BaseResponse.<T>builder()
                .code(1000)
                .message("Success")
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> ok(String message, T data) {
        return BaseResponse.<T>builder()
                .code(1000)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(int code, String message) {
        return BaseResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }
}
