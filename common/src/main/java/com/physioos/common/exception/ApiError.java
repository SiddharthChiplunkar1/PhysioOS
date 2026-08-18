package com.physioos.common.exception;

import lombok.Data;

@Data
public class ApiError {
    private String code;
    private String message;
    private String traceId;

    public ApiError() {
    }

    public ApiError(String code, String message, String traceId) {
        this.code = code;
        this.message = message;
        this.traceId = traceId;
    }
}
