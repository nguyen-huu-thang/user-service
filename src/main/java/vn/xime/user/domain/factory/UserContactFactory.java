package vn.xime.user.domain.factory;

import vn.xime.user.domain.model.ContactType;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserContact;

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

    private String normalize(ContactType type, String value) {
        return switch (type) {
            case EMAIL -> value.trim().toLowerCase();
            case PHONE -> value.trim();
            default -> value.trim();
        };
    }
}