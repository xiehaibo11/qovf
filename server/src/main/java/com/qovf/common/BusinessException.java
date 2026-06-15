package com.qovf.common;

/**
 * 业务异常：在业务代码中抛出后由 {@link GlobalExceptionHandler} 统一处理。
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(Result.ERROR, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
