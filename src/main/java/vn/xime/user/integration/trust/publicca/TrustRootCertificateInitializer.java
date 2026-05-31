package vn.xime.user.integration.trust.publicca;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vn.xime.user.infrastructure.security.store.RootCertificateFileStore;


/**
 * =========================================================
 * TRUST ROOT CERTIFICATE INITIALIZER
 * =========================================================
 *
 * Responsibilities:
 *
 * - load root CA certificate from filesystem
 * - initialize runtime RAM cache
 * - bootstrap trust anchor
 *
 * KHÔNG:
 *
 * - grpc synchronization
 * - certificate rotation
 * - scheduler logic
 * - certificate validation
 *
 * Root CA is considered:
 *
 * - stable
 * - long-lived
 * - startup trust anchor
 *
 * Therefore:
 *
 * - loaded once during startup
 * - cached entirely in RAM
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class TrustRootCertificateInitializer {

    /**
     * =====================================================
     * ROOT CERTIFICATE FILE STORE
     * =====================================================
     */
    private final RootCertificateFileStore rootCertificateFileStore;


    /**
     * =====================================================
     * ROOT CERTIFICATE RESOLVER
     * =====================================================
     */
    private final TrustRootCertificateResolver trustRootCertificateResolver;


    /**
     * =====================================================
     * INITIALIZE ROOT CERTIFICATE
     * =====================================================
     *
     * Startup bootstrap:
     *
     * file
     * →
     * RAM cache
     *
     * =====================================================
     */
    @PostConstruct
    public void initialize() {

        try {

            // =============================================
            // LOAD ROOT CERTIFICATE
            // =============================================

            String rootCertificate = rootCertificateFileStore.load();


            // =============================================
            // UPDATE RUNTIME CACHE
            // =============================================

            trustRootCertificateResolver.update(rootCertificate);

        } catch (Exception exception) {

            // TODO:
            // structured logging

            exception.printStackTrace();

            throw new IllegalStateException(
                "failed to initialize root certificate",
                exception
            );
        }
    }
}