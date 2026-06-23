package vn.xime.user.domain.user.policy;

/**
 * =========================================================
 * PASSWORD POLICY
 * =========================================================
 *
 * Quy tắc nghiệp vụ thuần về mật khẩu (độ dài tối thiểu...).
 * Đây là quyết định domain, không phải orchestration, nên
 * tách khỏi use case.
 *
 * TẠM THỜI đặt ở domain/user. Khi tách Credential aggregate
 * (xem CLAUDE.md), policy này nên chuyển sang domain credential.
 */
public class PasswordPolicy {

    private static final int MIN_LENGTH = 6;

    /**
     * Kiểm tra raw password có hợp lệ theo policy không.
     * Ném IllegalArgumentException nếu vi phạm (advice map -> 400).
     */
    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("credential is invalid");
        }

        if (rawPassword.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("password too short");
        }
    }
}
