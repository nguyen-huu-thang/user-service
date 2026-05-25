package vn.xime.user.infrastructure.scheduler;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.key.VerificationKeySynchronizer;


/**
 * =========================================================
 * TRUST VERIFICATION KEY SYNCHRONIZATION SCHEDULER
 * =========================================================
 *
 * Responsibility:
 *
 * - periodically synchronize verification keys
 * - trigger trust verification key refresh flow
 *
 * KHÔNG:
 *
 * - grpc logic
 * - cache logic
 * - database logic
 * - verification lookup logic
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class TrustVerificationKeySynchronizationScheduler {

    /**
     * =====================================================
     * TRUST VERIFICATION KEY SYNCHRONIZER
     * =====================================================
     */
    private final VerificationKeySynchronizer
        verificationKeySynchronizer;


    /**
     * =====================================================
     * STARTUP PRELOAD
     * =====================================================
     *
     * Runs immediately after Spring context initialized.
     *
     * =====================================================
     */
    @PostConstruct
    public void preload() {

        try {

            verificationKeySynchronizer
                .synchronize();

        } catch (Exception exception) {

            // TODO:
            // structured logging

            exception.printStackTrace();
        }
    }


    /**
     * =====================================================
     * PERIODIC SYNCHRONIZATION
     * =====================================================
     *
     * Offset a bit from signing key scheduler
     * to avoid simultaneous trust-service spikes.
     *
     * =====================================================
     */
    @Scheduled(
        fixedDelay = 5 * 24 * 60 * 60 * 1000
    )
    public void synchronize() {

        try {

            verificationKeySynchronizer
                .synchronize();

        } catch (Exception exception) {

            // TODO:
            // structured logging

            exception.printStackTrace();
        }
    }
}