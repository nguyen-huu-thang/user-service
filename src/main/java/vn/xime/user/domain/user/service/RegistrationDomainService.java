package vn.xime.user.domain.user.service;

import vn.xime.user.domain.authentication.model.IdentifierType;
import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.user.factory.UserContactFactory;
import vn.xime.user.domain.user.factory.UserFactory;
import vn.xime.user.domain.user.model.User;

/**
 * =========================================================
 * REGISTRATION DOMAIN SERVICE
 * =========================================================
 *
 * Chứa quy tắc nghiệp vụ "đăng ký dựa theo loại identifier":
 *
 * - USERNAME -> username = identifier, không tạo contact
 * - EMAIL    -> username = null, tạo primary email contact
 * - PHONE    -> username = null, tạo primary phone contact
 *
 * Đây là quyết định domain thuần (mapping identifierType ->
 * cấu trúc aggregate), trước đây nằm rải rác trong use case.
 *
 * Service không đụng tới repository/IO - chỉ dựng domain object;
 * việc lưu do tầng application đảm nhiệm.
 *
 * TẠM THỜI đặt ở domain/user. Khi tách Credential aggregate,
 * phần liên quan credential sẽ chuyển sang domain credential.
 */
public class RegistrationDomainService {

    private final UserFactory userFactory;
    private final UserContactFactory userContactFactory;

    public RegistrationDomainService(
            UserFactory userFactory,
            UserContactFactory userContactFactory
    ) {
        this.userFactory = userFactory;
        this.userContactFactory = userContactFactory;
    }

    /**
     * =====================================================
     * REGISTER
     * =====================================================
     *
     * @param identifierType       loại identifier
     * @param normalizedIdentifier identifier đã normalize
     * @param passwordHash         hash mật khẩu (đã hash ở application)
     * @return cặp User + UserContact (contact có thể null với USERNAME)
     */
    public RegistrationResult register(
            IdentifierType identifierType,
            String normalizedIdentifier,
            String passwordHash
    ) {
        if (identifierType == null) {
            throw new IllegalArgumentException("identifierType is required");
        }

        if (normalizedIdentifier == null || normalizedIdentifier.isBlank()) {
            throw new IllegalArgumentException("identifier is invalid");
        }

        String username = resolveUsername(identifierType, normalizedIdentifier);

        User user = userFactory.create(username, passwordHash);

        UserContact contact = resolveContact(
                identifierType,
                user,
                normalizedIdentifier
        );

        return new RegistrationResult(user, contact);
    }

    // =========================
    // USERNAME RULE
    // =========================

    private String resolveUsername(
            IdentifierType identifierType,
            String normalizedIdentifier
    ) {
        return switch (identifierType) {
            case USERNAME -> normalizedIdentifier;
            case EMAIL, PHONE -> null;
        };
    }

    // =========================
    // CONTACT RULE
    // =========================

    private UserContact resolveContact(
            IdentifierType identifierType,
            User user,
            String normalizedIdentifier
    ) {
        return switch (identifierType) {
            case EMAIL -> userContactFactory.createPrimary(
                    user.getId(), ContactType.EMAIL, normalizedIdentifier);
            case PHONE -> userContactFactory.createPrimary(
                    user.getId(), ContactType.PHONE, normalizedIdentifier);
            case USERNAME -> null;
        };
    }

    /**
     * =====================================================
     * REGISTRATION RESULT
     * =====================================================
     *
     * contact có thể null (đăng ký bằng USERNAME).
     */
    public record RegistrationResult(User user, UserContact contact) {
    }
}
