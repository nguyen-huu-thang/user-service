package vn.xime.user.domain.error;

/**
 * Centralized error catalog for user-service.
 * Catalog mã lỗi tập trung của user-service.
 *
 * Theo chuẩn platform (xem giới thiệu/.claude/docs/cross-cutting/quy-uoc-ma-loi-va-exception.md):
 * - common 000000-009999 (dùng chung + làm mã generic khi che lỗi)
 * - user-service block 040000-049999
 *   (Private 040000-043999 / System 044000-046999 / Public 047000-049999)
 *
 * Domain pure: không phụ thuộc thư viện nào (kể cả Lombok) - getter viết tay.
 * Dùng int httpStatus + GrpcCode để không phụ thuộc Spring/io.grpc.
 */
public enum ErrorCode {

    // ===== common - Private (lỗi hạ tầng, không ra ngoài) =====
    UNKNOWN("E000000", 0, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi không xác định"),
    INTERNAL_ERROR("E000001", 1, 500, GrpcCode.INTERNAL, Visibility.PRIVATE, "Lỗi nội bộ hệ thống"),

    // ===== common - Public (an toàn cho client, dùng làm generic khi che) =====
    BAD_REQUEST("E007000", 7000, 400, GrpcCode.INVALID_ARGUMENT, Visibility.PUBLIC, "Yêu cầu không hợp lệ"),
    VALIDATION_FAILED("E007001", 7001, 400, GrpcCode.INVALID_ARGUMENT, Visibility.PUBLIC, "Dữ liệu đầu vào không hợp lệ"),
    UNAUTHENTICATED("E007002", 7002, 401, GrpcCode.UNAUTHENTICATED, Visibility.PUBLIC, "Chưa xác thực"),
    FORBIDDEN("E007004", 7004, 403, GrpcCode.PERMISSION_DENIED, Visibility.PUBLIC, "Không có quyền truy cập"),
    NOT_FOUND("E007005", 7005, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy tài nguyên"),
    ALREADY_EXISTS("E007006", 7006, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Tài nguyên đã tồn tại"),
    RULE_VIOLATION("E007007", 7007, 422, GrpcCode.FAILED_PRECONDITION, Visibility.PUBLIC, "Vi phạm ràng buộc nghiệp vụ"),
    TOO_MANY_REQUESTS("E007008", 7008, 429, GrpcCode.RESOURCE_EXHAUSTED, Visibility.PUBLIC, "Quá nhiều yêu cầu"),

    // ===== user-service - Public (047xxx) =====
    INVALID_IDENTITY_ID("E047000", 47000, 400, GrpcCode.INVALID_ARGUMENT, Visibility.PUBLIC, "Mã định danh không hợp lệ"),
    IDENTIFIER_ALREADY_EXISTS("E047001", 47001, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Định danh đã tồn tại"),
    PROFILE_NOT_FOUND("E047010", 47010, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy hồ sơ người dùng"),
    PROFILE_ALREADY_EXISTS("E047011", 47011, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Hồ sơ người dùng đã tồn tại"),
    CONTACT_NOT_FOUND("E047020", 47020, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy liên hệ"),
    CONTACT_ALREADY_EXISTS("E047021", 47021, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Liên hệ đã tồn tại"),
    ADDRESS_NOT_FOUND("E047030", 47030, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy địa chỉ"),
    LINK_NOT_FOUND("E047040", 47040, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy liên kết"),
    LINK_ALREADY_EXISTS("E047041", 47041, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Liên kết đã tồn tại"),
    INTEREST_NOT_FOUND("E047050", 47050, 404, GrpcCode.NOT_FOUND, Visibility.PUBLIC, "Không tìm thấy sở thích"),
    INTEREST_ALREADY_ADDED("E047051", 47051, 409, GrpcCode.ALREADY_EXISTS, Visibility.PUBLIC, "Sở thích đã được thêm");

    private final String errorKey;
    private final int code;
    private final int httpStatus;
    private final GrpcCode grpcCode;
    private final Visibility visibility;
    private final String message;

    ErrorCode(String errorKey, int code, int httpStatus,
              GrpcCode grpcCode, Visibility visibility, String message) {
        this.errorKey = errorKey;
        this.code = code;
        this.httpStatus = httpStatus;
        this.grpcCode = grpcCode;
        this.visibility = visibility;
        this.message = message;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public int getCode() {
        return code;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public GrpcCode getGrpcCode() {
        return grpcCode;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Generic common code in the same status family, used when redacting a hidden error.
     * Mã common cùng họ trạng thái, dùng khi che một lỗi không được phép phơi bày.
     */
    public static ErrorCode genericFor(ErrorCode ec) {
        return switch (ec.grpcCode) {
            case INVALID_ARGUMENT -> BAD_REQUEST;
            case NOT_FOUND -> NOT_FOUND;
            case ALREADY_EXISTS -> ALREADY_EXISTS;
            case PERMISSION_DENIED -> FORBIDDEN;
            case UNAUTHENTICATED -> UNAUTHENTICATED;
            case FAILED_PRECONDITION -> RULE_VIOLATION;
            case RESOURCE_EXHAUSTED -> TOO_MANY_REQUESTS;
            default -> UNKNOWN;
        };
    }
}
