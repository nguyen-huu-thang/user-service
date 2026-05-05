package vn.xime.user.domain.model;

import java.time.Instant;
import java.util.Objects;

public class UserUsernameHistory {

    private final Id id;
    private final Id userId;

    private final String oldUsername;

    private final Instant changedAt;

    public UserUsernameHistory(
            Id id,
            Id userId,
            String oldUsername,
            Instant changedAt
    ) {
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.oldUsername = Objects.requireNonNull(oldUsername);
        this.changedAt = Objects.requireNonNull(changedAt);

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        if (oldUsername.isBlank()) {
            throw new IllegalArgumentException("oldUsername must not be blank");
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isBefore(Instant instant) {
        return changedAt.isBefore(instant);
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

    public String getOldUsername() {
        return oldUsername;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}