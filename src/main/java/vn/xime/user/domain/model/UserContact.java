package vn.xime.user.domain.model;

import java.time.Instant;
import java.util.Objects;

public class UserContact {

    private final Id id;
    private final Id userId;

    private final ContactType type;
    private final String value;

    private final boolean isVerified;
    private final boolean isPrimary;

    private final Instant createdAt;

    public UserContact(
            Id id,
            Id userId,
            ContactType type,
            String value,
            boolean isVerified,
            boolean isPrimary,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
        this.isVerified = isVerified;
        this.isPrimary = isPrimary;
        this.createdAt = Objects.requireNonNull(createdAt);

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        if (value.isBlank()) {
            throw new IllegalArgumentException("value must not be blank");
        }

        // validation nhẹ (không quá strict)
        switch (type) {
            case EMAIL -> {
                if (!value.contains("@")) {
                    throw new IllegalArgumentException("invalid email format");
                }
            }
            case PHONE -> {
                if (value.length() < 6) {
                    throw new IllegalArgumentException("invalid phone number");
                }
            }
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isVerified() {
        return isVerified;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public boolean isSameType(ContactType otherType) {
        return this.type == otherType;
    }

    // =========================
    // STATE CHANGE
    // =========================

    public UserContact markVerified() {
        if (isVerified) {
            return this;
        }

        return new UserContact(
                id,
                userId,
                type,
                value,
                true,
                isPrimary,
                createdAt
        );
    }

    public UserContact markPrimary() {
        if (isPrimary) {
            return this;
        }

        return new UserContact(
                id,
                userId,
                type,
                value,
                isVerified,
                true,
                createdAt
        );
    }

    public UserContact unmarkPrimary() {
        if (!isPrimary) {
            return this;
        }

        return new UserContact(
                id,
                userId,
                type,
                value,
                isVerified,
                false,
                createdAt
        );
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public Id getUserId() {
        return userId;
    }

    public ContactType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}