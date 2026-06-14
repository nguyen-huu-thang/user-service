package vn.xime.user.api.grpc.error;

import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vn.xime.user.domain.sharedkernel.error.AppException;
import vn.xime.user.domain.sharedkernel.error.Channel;
import vn.xime.user.domain.sharedkernel.error.ErrorCode;
import vn.xime.user.domain.sharedkernel.error.ErrorRedactor;
import vn.xime.user.domain.sharedkernel.error.Visibility;

/**
 * Shared gRPC exception mapper - one place for all internal gRPC APIs.
 * Mapper exception gRPC dùng chung - một nơi cho mọi gRPC API nội bộ.
 *
 * Map exception -> ErrorCode, che theo kênh GRPC_INTERNAL, gắn metadata
 * xime-error / xime-error-code, trả Status an toàn (không lộ stack trace).
 */
public final class GrpcErrorMapper {

    private static final Logger log = LoggerFactory.getLogger(GrpcErrorMapper.class);

    private static final Metadata.Key<String> ERROR_KEY =
        Metadata.Key.of("xime-error", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata.Key<String> ERROR_CODE_KEY =
        Metadata.Key.of("xime-error-code", Metadata.ASCII_STRING_MARSHALLER);

    private GrpcErrorMapper() {
    }

    public static StatusRuntimeException toStatus(Throwable ex) {
        ErrorCode original = resolve(ex);

        if (original.getVisibility() == Visibility.PRIVATE) {
            // Lỗi nội bộ / không xác định: log đầy đủ, ngoài chỉ thấy mã generic
            log.error("Internal error in gRPC: {}", original.getErrorKey(), ex);
        }

        ErrorCode ec = ErrorRedactor.forChannel(original, Channel.GRPC_INTERNAL);
        String description = original.getVisibility() == Visibility.PUBLIC
            ? coalesce(ex.getMessage(), original.getMessage())
            : ec.getMessage();

        Metadata trailers = new Metadata();
        trailers.put(ERROR_KEY, ec.getErrorKey());
        trailers.put(ERROR_CODE_KEY, String.valueOf(ec.getCode()));

        return Status.fromCode(Status.Code.valueOf(ec.getGrpcCode().name()))
            .withDescription(description)
            .asRuntimeException(trailers);
    }

    private static ErrorCode resolve(Throwable ex) {
        if (ex instanceof AppException appException) {
            return appException.getErrorCode();
        }
        if (ex instanceof IllegalArgumentException) {
            return ErrorCode.VALIDATION_FAILED;
        }
        if (ex instanceof IllegalStateException) {
            return ErrorCode.RULE_VIOLATION;
        }
        return ErrorCode.UNKNOWN;
    }

    private static String coalesce(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value;
    }
}
