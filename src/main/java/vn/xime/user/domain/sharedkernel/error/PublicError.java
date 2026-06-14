package vn.xime.user.domain.sharedkernel.error;

/**
 * Error safe to expose to the browser / REST external (lowest security).
 * Lỗi an toàn để phơi ra browser / REST external (bảo mật thấp nhất).
 *
 * Hiển thị nguyên vẹn ở mọi kênh.
 */
public class PublicError extends AppException {

    public PublicError(ErrorCode errorCode) {
        super(errorCode);
    }

    public PublicError(ErrorCode errorCode, String overrideMessage) {
        super(errorCode, overrideMessage);
    }
}
