package vn.xime.user.domain.user.model;

import java.time.Instant;
import java.util.Objects;

import vn.xime.user.domain.sharedkernel.model.Id;

public class User {

    private final Id id;

    private final String username;
    private final String passwordHash;

    private final UserStatus status;

    private final Instant createdAt;
    private final Instant updatedAt;

    public User(
            Id id,
            String username,
            String passwordHash,
            UserStatus status,
            Instant createdAt,
            Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id);
        // username nullable: user đăng ký bằng email/phone chưa có username.
        // username nullable: register by email/phone may have no username yet.
        this.username = username;
        this.passwordHash = Objects.requireNonNull(passwordHash);
        this.status = Objects.requireNonNull(status);
        this.createdAt = Objects.requireNonNull(createdAt);
        this.updatedAt = updatedAt;

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        // username có thể null (đăng ký bằng email/phone), nhưng nếu
        // có thì không được rỗng.
        // username may be null (email/phone signup), but must not be blank if present.
        if (username != null && username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    public boolean isLocked() {
        return status == UserStatus.LOCKED;
    }

    public boolean isDeleted() {
        return status == UserStatus.DELETED;
    }

    // =========================
    // STATE CHANGE
    // =========================

    public User changeUsername(String newUsername, Instant now) {
        if (isDeleted()) {
            throw new IllegalStateException("Cannot change username of deleted user");
        }

        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }

        return new User(
                id,
                newUsername,
                passwordHash,
                status,
                createdAt,
                now
        );
    }

    public User changePassword(String newPasswordHash, Instant now) {
        if (isDeleted()) {
            throw new IllegalStateException("Cannot change password of deleted user");
        }

        return new User(
                id,
                username,
                Objects.requireNonNull(newPasswordHash),
                status,
                createdAt,
                now
        );
    }

    public User lock(Instant now) {
        if (isDeleted()) {
            throw new IllegalStateException("Cannot lock deleted user");
        }

        return new User(
                id,
                username,
                passwordHash,
                UserStatus.LOCKED,
                createdAt,
                now
        );
    }

    public User activate(Instant now) {
        if (isDeleted()) {
            throw new IllegalStateException("Cannot activate deleted user");
        }

        return new User(
                id,
                username,
                passwordHash,
                UserStatus.ACTIVE,
                createdAt,
                now
        );
    }

    public User delete(Instant now) {
        return new User(
                id,
                username,
                passwordHash,
                UserStatus.DELETED,
                createdAt,
                now
        );
    }

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}