package vn.xime.user.integration.trust.publicca;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;


/**
 * =========================================================
 * TRUST ROOT CERTIFICATE RESOLVER
 * =========================================================
 *
 * Runtime in-memory root CA cache.
 *
 * Responsibilities:
 *
 * - cache current root certificate in RAM
 * - provide fast runtime lookup
 * - expose cache update methods
 *
 * KHÔNG:
 *
 * - grpc communication
 * - filesystem persistence
 * - scheduler logic
 * - trust-service orchestration
 * - certificate validation
 *
 * Background refresh is handled externally.
 *
 * =========================================================
 */
@Component
public class TrustRootCertificateResolver {

    /**
     * =====================================================
     * CURRENT ROOT CERTIFICATE
     * =====================================================
     *
     * PEM format.
     *
     * =====================================================
     */
    private final AtomicReference<String> rootCertificate =
        new AtomicReference<>();


    /**
     * =====================================================
     * RESOLVE ROOT CERTIFICATE
     * =====================================================
     */
    public Optional<String> resolve() {

        return Optional.ofNullable(
            rootCertificate.get()
        );
    }


    /**
     * =====================================================
     * UPDATE ROOT CERTIFICATE
     * =====================================================
     */
    public void update(
        String rootCert
    ) {

        rootCertificate.set(
            rootCert
        );
    }


    /**
     * =====================================================
     * CLEAR CACHE
     * =====================================================
     */
    public void clear() {

        rootCertificate.set(
            null
        );
    }
}