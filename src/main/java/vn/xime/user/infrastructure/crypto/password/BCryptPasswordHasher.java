package vn.xime.user.infrastructure.crypto.password;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import vn.xime.user.application.port.out.crypto.PasswordHasher;


/**
 * =========================================================
 * BCRYPT PASSWORD HASHER
 * =========================================================
 *
 * BCrypt implementation cho PasswordHasher.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * - hash raw password
 * - verify password hash
 *
 * =========================================================
 * SECURITY
 * =========================================================
 *
 * BCrypt:
 *
 * - tự generate salt
 * - embed salt trong hash
 * - adaptive cost
 * - chống rainbow table attack
 *
 * =========================================================
 * HASH FORMAT
 * =========================================================
 *
 * Ví dụ:
 *
 * $2a$10$abcdefghijklmnopqrstuv...
 *
 * Format chứa:
 *
 * - algorithm metadata
 * - cost factor
 * - salt
 * - hash
 *
 * =========================================================
 * IMPORTANT
 * =========================================================
 *
 * Raw password:
 *
 * - KHÔNG được log
 * - KHÔNG được cache
 * - KHÔNG được persist
 *
 * =========================================================
 * FUTURE
 * =========================================================
 *
 * Sau này có thể:
 *
 * - migrate sang Argon2id
 * - support algorithm upgrade
 * - configurable strength
 * - rolling rehash strategy
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class BCryptPasswordHasher
    implements PasswordHasher {

    /**
     * =====================================================
     * BCRYPT ENCODER
     * =====================================================
     *
     * BCrypt implementation của Spring Security.
     *
     * =====================================================
     * DEFAULT STRENGTH
     * =====================================================
     *
     * Current:
     *
     * 10
     *
     * Higher strength:
     *
     * - an toàn hơn
     * - nhưng tốn CPU hơn
     *
     * =====================================================
     */
    private final BCryptPasswordEncoder
        passwordEncoder;


    /**
     * =====================================================
     * HASH PASSWORD
     * =====================================================
     */
    @Override
    public String hash(
        String rawPassword
    ) {

        if (rawPassword == null
            || rawPassword.isBlank()) {

            throw new IllegalArgumentException(
                "rawPassword is invalid"
            );
        }

        return passwordEncoder.encode(
            rawPassword
        );
    }


    /**
     * =====================================================
     * VERIFY PASSWORD
     * =====================================================
     */
    @Override
    public boolean matches(
        String rawPassword,
        String passwordHash
    ) {

        if (rawPassword == null
            || rawPassword.isBlank()) {

            return false;
        }

        if (passwordHash == null
            || passwordHash.isBlank()) {

            return false;
        }

        return passwordEncoder.matches(
            rawPassword,
            passwordHash
        );
    }
}