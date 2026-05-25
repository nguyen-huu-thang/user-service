package vn.xime.user.integration.trust.key;

import java.time.Instant;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;


import vn.xime.user.infrastructure.persistence.repository.KeyContextRepository;


@Component
@RequiredArgsConstructor
public class TrustKeyCleanup {
        /**
     * =====================================================
     * DATABASE REPOSITORY
     * =====================================================
     */
    private final KeyContextRepository keyRepository;

    public void cleanUp() {
        /*
         * =================================================
         * CLEAN UP DATABASE
         * =================================================
         *
         * Xóa các key đã hết hạn từ database.
         *
         */
        keyRepository.deleteExpiredKeys(Instant.now());
    }
}
