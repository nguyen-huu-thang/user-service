package vn.xime.user.domain.user.factory;

import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.User;
import vn.xime.user.domain.user.model.UserStatus;

import java.time.Instant;

public class UserFactory {

    public User create(
            String username,
            String passwordHash
    ) {
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        // username nullable: user đăng ký bằng email/phone chưa có username.
        // Nếu có thì không được rỗng.
        // username nullable (email/phone signup); must not be blank if present.
        if (username != null && username.isBlank()) {
            throw new IllegalArgumentException("username must not be blank");
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