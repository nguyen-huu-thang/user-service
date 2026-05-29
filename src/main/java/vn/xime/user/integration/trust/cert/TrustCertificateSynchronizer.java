package vn.xime.user.integration.trust.cert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vn.xime.user.infrastructure.grpc.trust.cert.GrpcTrustCertificateClient;

import vn.xime.user.infrastructure.security.bootstrap.Bootstrap;

import vn.xime.user.integration.trust.model.Certificate;

import vn.xime.user.infrastructure.persistence.repository.CertificateRepository;


/**
 * =========================================================
 * TRUST CERTIFICATE SYNCHRONIZER
 * =========================================================
 *
 * Responsibilities:
 *
 * - determine startup trust state
 * - bootstrap runtime certificate
 * - synchronize certificate with trust-service
 * - persist runtime certificate
 * - update runtime RAM resolver
 * - cleanup bootstrap credential
 *
 * KHÔNG:
 *
 * - scheduler trigger
 * - grpc transport implementation
 * - runtime certificate lookup
 * - trust-service lifecycle management
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class TrustCertificateSynchronizer {

    /**
     * =====================================================
     * TRUST GRPC CLIENT
     * =====================================================
     */
    private final GrpcTrustCertificateClient grpcTrustCertificateClient;


    /**
     * =====================================================
     * RUNTIME RESOLVER
     * =====================================================
     */
    private final TrustCertificateResolver trustCertificateResolver;


    /**
     * =====================================================
     * DATABASE REPOSITORY
     * =====================================================
     */
    private final CertificateRepository certificateRepository;


    /**
     * =====================================================
     * BOOTSTRAP
     * =====================================================
     */
    private final Bootstrap bootstrap;


    /**
     * =====================================================
     * STARTUP SYNCHRONIZATION
     * =====================================================
     *
     * State model:
     *
     * NEW
     * - bootstrap exists
     * - db cert missing
     *
     * ACTIVE
     * - bootstrap missing
     * - db cert exists
     *
     * RECOVERABLE
     * - bootstrap exists
     * - db cert exists
     *
     * BROKEN
     * - bootstrap missing
     * - db cert missing
     *
     * =====================================================
     */
    public void synchronizeOnStartup() {

        boolean hasBootstrap = bootstrap.exists();

        Optional<Certificate> databaseCertificate = certificateRepository.findLatest();

        boolean hasDatabaseCertificate = databaseCertificate.isPresent();

        // =================================================
        // BROKEN
        // =================================================

        if (
            !hasBootstrap
            && !hasDatabaseCertificate
        ) {

            throw new IllegalStateException(
                """
                \n==================================================
                FATAL TRUST STARTUP ERROR
                ==================================================

                No bootstrap file found.
                No runtime certificate found in database.

                System cannot establish trust.

                Bootstrap file không tồn tại.
                Database không có runtime certificate.

                Hệ thống không thể thiết lập trust.

                ==================================================
                """
            );
        }

        // =================================================
        // NEW
        // =================================================

        if (
            hasBootstrap
            && !hasDatabaseCertificate
        ) {

            synchronizeBootstrap();

            return;
        }

        // =================================================
        // ACTIVE
        // =================================================

        if (
            !hasBootstrap
            && hasDatabaseCertificate
        ) {

            synchronizeRuntime(
                databaseCertificate.get()
            );

            return;
        }

        // =================================================
        // RECOVERABLE
        // =================================================

        synchronizeRecoverable();
    }


    /**
     * =====================================================
     * SCHEDULED SYNCHRONIZATION
     * =====================================================
     */
    public void synchronize() {

        Optional<Certificate> certificate =
            certificateRepository
                .findLatest();

        if (certificate.isEmpty()) {
            return;
        }

        synchronizeRuntime(
            certificate.get()
        );

        certificateRepository.deleteExpiredCertificates(Instant.now());
    }


    /**
     * =====================================================
     * BOOTSTRAP FLOW
     * =====================================================
     */
    private void synchronizeBootstrap() {

        Certificate bootstrapCertificate = bootstrap.load();

        Certificate rotatedCertificate =
            rotateCertificate(
                bootstrapCertificate
            );

        publish(
            rotatedCertificate
        );

        bootstrap.delete();
    }


    /**
     * =====================================================
     * RUNTIME FLOW
     * =====================================================
     */
    private void synchronizeRuntime(
        Certificate currentCertificate
    ) {

        // ================================================
        // UPDATE RUNTIME CACHE IMMEDIATELY
        // ================================================

        trustCertificateResolver.update(
            currentCertificate
        );

        // ================================================
        // ROTATE AFTER 5 MONTHS
        // ================================================

        Instant now = Instant.now();

        Instant issuedAt = currentCertificate.issuedAt();

        if (
            issuedAt != null
            && issuedAt.plus(
                150,
                ChronoUnit.DAYS
            ).isAfter(now)
        ) {

            return;
        }

        try {

            Certificate rotatedCertificate =
                rotateCertificate(
                    currentCertificate
                );

            publish(
                rotatedCertificate
            );

        } catch (RuntimeException exception) {

            // ============================================
            // KEEP CURRENT CERTIFICATE
            // ============================================

            // Runtime continues using current valid cert.
        }
    }


    /**
     * =====================================================
     * RECOVERABLE FLOW
     * =====================================================
     */
    private void synchronizeRecoverable() {
        certificateRepository.deleteAll();
        synchronizeBootstrap();
    }


    /**
     * =====================================================
     * ROTATE CERTIFICATE
     * =====================================================
     */
    private Certificate rotateCertificate(
        Certificate certificate
    ) {

        return grpcTrustCertificateClient
            .rotateCertificate(

                certificate.refreshTokenId(),

                certificate.refreshToken(),

                certificate.privateKey()
            );
    }


    /**
     * =====================================================
     * PUBLISH CERTIFICATE
     * =====================================================
     */
    private void publish(
        Certificate certificate
    ) {

        certificateRepository.save(
            certificate
        );

        trustCertificateResolver.update(
            certificate
        );
    }
}
