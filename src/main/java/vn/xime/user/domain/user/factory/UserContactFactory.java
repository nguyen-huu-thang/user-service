package vn.xime.user.domain.user.factory;

import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.time.Instant;

public class UserContactFactory {

    public UserContact create(
            Id userId,
            ContactType type,
            String value
    ) {
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("value is required");
        }

        // =========================
        // NORMALIZE
        // =========================

        String normalized = normalize(type, value);

        // =========================
        // BUILD DOMAIN
        // =========================

        Id id = IdFactory.generate();

        Instant now = Instant.now();

        return new UserContact(
                id,
                userId,
                type,
                normalized,
                false, // not verified
                false, // not primary
                now
        );
    }

    /**
     * =========================
     * CREATE PRIMARY
     * =========================
     *
     * Tạo contact và đánh dấu primary ngay - dùng cho luồng
     * đăng ký bằng email/phone, contact đầu tiên mặc định là
     * primary của user.
     */
    public UserContact createPrimary(
            Id userId,
            ContactType type,
            String value
    ) {
        return create(userId, type, value).markPrimary();
    }

    private String normalize(ContactType type, String value) {
        return switch (type) {
            case EMAIL -> value.trim().toLowerCase();
            case PHONE -> value.trim();
            default -> value.trim();
        };
    }
}