package vn.xime.user.domain.sharedkernel.error;

/**
 * Error that other services may read over internal gRPC, but not the browser.
 * Lỗi mà service khác đọc được qua gRPC nội bộ, nhưng không phơi ra browser.
 *
 * Adapter REST external che thành mã generic; gRPC nội bộ giữ nguyên.
 */
public class SystemError extends AppException {

    public SystemError(ErrorCode errorCode) {
        super(errorCode);
    }

    public SystemError(ErrorCode errorCode, String overrideMessage) {
        super(errorCode, overrideMessage);
    }
}
