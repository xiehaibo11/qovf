package com.qovf.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：统一捕获并以 {@link Result} 形式返回。
 * 所有异常都会记录「请求位置 + 完整堆栈」到日志（含 logs/qovf-error.log），便于定位错误发生在哪里。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 组装「方法 URI（来源IP）」上下文，便于在日志中定位错误现场 */
    private String at(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI()
                + " (from " + request.getRemoteAddr() + ")";
    }

    /** 参数校验失败（@Valid） */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        // 校验类错误是可预期的，warn 级即可，但仍记录发生位置
        log.warn("参数校验失败 @ {} -> {}", at(request), message);
        return Result.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    /** 业务异常 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e, HttpServletRequest request) {
        // 业务异常通常无需完整堆栈，但要记清发生在哪个接口
        log.warn("业务异常 @ {} -> code={}, msg={}", at(request), e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /** 兜底异常：未预期错误，记录完整堆栈定位源头 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        // 第三个参数为 Throwable，logback 会输出完整堆栈（类/方法/行号），写入 logs/qovf-error.log
        log.error("系统异常 @ {} -> {}", at(request), e.getMessage(), e);
        return Result.fail("系统繁忙，请稍后再试");
    }
}
