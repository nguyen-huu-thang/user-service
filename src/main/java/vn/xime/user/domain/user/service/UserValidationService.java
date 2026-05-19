package vn.xime.user.domain.user.service;

import vn.xime.user.domain.user.model.User;


public class UserValidationService {

    // =========================
    // CREATE VALIDATION
    // =========================

    public void validateNewUser(
            String username,
            String passwordHash
    ) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash is required");
        }
    }

    // =========================
    // STATE VALIDATION
    // =========================

    public void ensureNotDeleted(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        if (user.isDeleted()) {
            throw new IllegalStateException("User is deleted");
        }
    }

    // =========================
    // UPDATE VALIDATION
    // =========================

    public void validateUsernameChange(
            User user,
            String newUsername
    ) {
        ensureNotDeleted(user);

        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
        }
    }

    public void validatePasswordChange(
            User user,
            String newPasswordHash
    ) {
        ensureNotDeleted(user);

        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash must not be blank");
        }
    }

    // =========================
    // BUSINESS RULE (OPTIONAL)
    // =========================

    public void ensureActive(User user) {
        if (!user.isActive()) {
            throw new IllegalStateException("User is not active");
        }
    }
}