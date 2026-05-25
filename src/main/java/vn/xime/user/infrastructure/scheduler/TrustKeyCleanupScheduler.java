package vn.xime.user.infrastructure.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.key.TrustKeyCleanup;


/**
 * =========================================================
 * TRUST KEY CLEANUP SCHEDULER
 * =========================================================
 *
 * Responsibility:
 *
 * - periodically cleanup expired keys
 *
 * KHÔNG:
 *
 * - database cleanup logic
 * - key synchronization logic
 * - grpc logic
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class TrustKeyCleanupScheduler {

    /**
     * =====================================================
     * CLEANUP SERVICE
     * =====================================================
     */
    private final TrustKeyCleanup keyCleanup;


    /**
     * =====================================================
     * PERIODIC CLEANUP
     * =====================================================
     *
     * Run:
     *
     * - every 30 days
     *
     * =====================================================
     */
    @Scheduled(
        fixedDelay = 30L * 24 * 60 * 60 * 1000
    )
    public void cleanUp() {

        try {

            keyCleanup.cleanUp();

        } catch (Exception exception) {

            // TODO:
            // structured logging

            exception.printStackTrace();
        }
    }
}