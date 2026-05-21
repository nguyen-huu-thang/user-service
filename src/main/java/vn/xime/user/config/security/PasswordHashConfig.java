package vn.xime.user.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.crypto.bcrypt
    .BCryptPasswordEncoder;


/**
 * =========================================================
 * PASSWORD HASH CONFIG
 * =========================================================
 */
@Configuration
public class PasswordHashConfig {

    /**
     * =====================================================
     * BCRYPT PASSWORD ENCODER
     * =====================================================
     */
    @Bean
    public BCryptPasswordEncoder
    bcryptPasswordEncoder() {

        return new BCryptPasswordEncoder(
            10
        );
    }
}