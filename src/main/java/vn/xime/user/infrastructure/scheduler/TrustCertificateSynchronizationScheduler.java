package vn.xime.user.infrastructure.scheduler;

import jakarta.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.cert.TrustCertificateSynchronizer;


/**
 * =========================================================
 * TRUST CERTIFICATE SYNCHRONIZATION SCHEDULER
 * =========================================================
 *
 * Responsibility:
 *
 * - startup trust synchronization
 * - periodically synchronize runtime certificate
 * - trigger certificate rotation flow
 *
 * KHÔNG:
 *
 * - grpc logic
 * - bootstrap validation
 * - database logic
 * - resolver logic
 * - certificate rotation logic
 * - trust state resolution
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class TrustCertificateSynchronizationScheduler {

    /**
     * =====================================================
     * TRUST CERTIFICATE SYNCHRONIZER
     * =====================================================
     */
    private final TrustCertificateSynchronizer
        trustCertificateSynchronizer;


    /**
     * =====================================================
     * STARTUP SYNCHRONIZATION
     * =====================================================
     *
     * Runs immediately after Spring context initialized.
     *
     * =====================================================
     */
    @PostConstruct
    public void startup() {

        try {

            trustCertificateSynchronizer
                .synchronizeOnStartup();

        } catch (Exception exception) {

            // =============================================
            // TODO:
            // structured logging
            // =============================================

            exception.printStackTrace();

            // =============================================
            // FATAL STARTUP FAILURE
            // =============================================

            throw exception;
        }
    }


    /**
     * =====================================================
     * PERIODIC SYNCHRONIZATION
     * =====================================================
     *
     * Runtime certificate synchronization.
     *
     * =====================================================
     */
    @Scheduled(
        fixedDelay = 24*60*60*1000
    )
    public void synchronize() {

        try {

            trustCertificateSynchronizer
                .synchronize();

        } catch (Exception exception) {

            // =============================================
            // TODO:
            // structured logging
            // =============================================

            exception.printStackTrace();
        }
    }
}

