package vn.xime.user.domain.factory;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.User;
import vn.xime.user.domain.model.UserStatus;

import java.time.Instant;

public class UserFactory {

    public User create(
            String username,
            String passwordHash
    ) {
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username is required");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash is required");
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        Id id = IdFactory.generate(); // 🔥 KSUID

        Instant now = Instant.now();

        return new User(
                id,
                username,
                passwordHash,
                UserStatus.ACTIVE, // default
                now,
                now
        );
    }
}