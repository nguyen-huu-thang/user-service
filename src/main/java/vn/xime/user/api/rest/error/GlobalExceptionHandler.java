package vn.xime.user.api.rest.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.xime.user.domain.sharedkernel.error.AppException;
import vn.xime.user.domain.sharedkernel.error.Channel;
import vn.xime.user.domain.sharedkernel.error.ErrorCode;
import vn.xime.user.domain.sharedkernel.error.ErrorRedactor;
import vn.xime.user.domain.sharedkernel.error.Visibility;

/**
 * Global REST exception handler - maps exceptions to the standard error body.
 * Bộ xử lý exception REST toàn cục - map exception sang body lỗi chuẩn.
 *
 * Mọi lỗi đi qua đây: AppException theo catalog (có che theo visibility),
 * lỗi @Valid và lỗi validate domain -> 400, còn lại -> E000000 (không lộ chi tiết).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Application exception with an ErrorCode (redacted for REST external).
     * Exception ứng dụng mang ErrorCode (đã che cho kênh REST external).
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleApp(AppException ex) {
        ErrorCode original = ex.getErrorCode();
        if (original.getVisibility() != Visibility.PUBLIC) {
            log.error("Non-public error reached REST: {}", original.getErrorKey(), ex);
        }
        ErrorCode ec = ErrorRedactor.forChannel(original, Channel.REST_EXTERNAL);
        String message = original.getVisibility() == Visibility.PUBLIC
            ? coalesce(ex.getMessage(), original.getMessage())
            : ec.getMessage();
        return toResponse(ec, message);
    }

    /**
     * Bean validation (@Valid) failure.
     * Lỗi validate body theo @Valid.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBeanValidation(MethodArgumentNotValidException ex) {
        ErrorCode ec = ErrorCode.VALIDATION_FAILED;
        String message = ex.getBindingResult().getFieldError() != null
            ? ex.getBindingResult().getFieldError().getField() + ": "
              + ex.getBindingResult().getFieldError().getDefaultMessage()
            : ec.getMessage();
        return toResponse(ec, message);
    }

    /**
     * Domain validation throwing IllegalArgumentException -> 400.
     * Validate trong domain ném IllegalArgumentException -> 400.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ErrorCode ec = ErrorCode.VALIDATION_FAILED;
        return toResponse(ec, coalesce(ex.getMessage(), ec.getMessage()));
    }

    /**
     * Domain invariant violation throwing IllegalStateException -> 422.
     * Vi phạm bất biến domain ném IllegalStateException -> 422.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        ErrorCode ec = ErrorCode.RULE_VIOLATION;
        return toResponse(ec, coalesce(ex.getMessage(), ec.getMessage()));
    }

    /**
     * Fallback - never leak details.
     * Dự phòng - không bao giờ lộ chi tiết.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        log.error("Unhandled exception", ex);
        ErrorCode ec = ErrorCode.UNKNOWN;
        return toResponse(ec, ec.getMessage());
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode ec, String message) {
        return ResponseEntity
            .status(ec.getHttpStatus())
            .body(new ErrorResponse(ec.getErrorKey(), ec.getCode(), message));
    }

    private static String coalesce(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
