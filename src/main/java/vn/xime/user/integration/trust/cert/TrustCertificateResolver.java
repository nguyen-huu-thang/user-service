package vn.xime.user.integration.trust.cert;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.stereotype.Component;

import vn.xime.user.integration.trust.model.Certificate;


/**
 * =========================================================
 * TRUST CERTIFICATE RESOLVER
 * =========================================================
 *
 * Runtime in-memory certificate cache.
 *
 * Responsibilities:
 *
 * - cache current runtime certificate in RAM
 * - provide fast runtime lookup
 * - expose cache update methods
 * - cleanup expired certificate
 *
 * KHÔNG:
 *
 * - grpc communication
 * - filesystem persistence
 * - database persistence
 * - scheduler logic
 * - trust-service orchestration
 *
 * Background refresh is handled externally.
 *
 * =========================================================
 */
@Component
public class TrustCertificateResolver {

    /**
     * =====================================================
     * CURRENT CERTIFICATE
     * =====================================================
     *
     * Runtime active certificate.
     *
     * =====================================================
     */
    private final AtomicReference<Certificate> certificate =
        new AtomicReference<>();


    /**
     * =====================================================
     * RESOLVE CERTIFICATE
     * =====================================================
     */
    public Optional<Certificate> resolve() {

        return Optional.ofNullable(
            certificate.get()
        );
    }


    /**
     * =====================================================
     * UPDATE CERTIFICATE
     * =====================================================
     *
     * New certificate replaces old certificate entirely.
     *
     * =====================================================
     */
    public void update(
        Certificate newCertificate
    ) {

        certificate.set(
            newCertificate
        );
    }


    /**
     * =====================================================
     * REMOVE CERTIFICATE
     * =====================================================
     */
    public void remove() {

        certificate.set(
            null
        );
    }


    /**
     * =====================================================
     * CLEAN EXPIRED CERTIFICATE
     * =====================================================
     */
    public void cleanExpiredCertificate() {

        Certificate current =
            certificate.get();

        if (current == null) {
            return;
        }

        Instant expiresAt =
            current.expiresAt();

        if (
            expiresAt != null
            && expiresAt.isBefore(
                Instant.now()
            )
        ) {

            certificate.set(
                null
            );
        }
    }


    /**
     * =====================================================
     * HAS CERTIFICATE
     * =====================================================
     */
    public boolean hasCertificate() {

        return certificate.get() != null;
    }


    /**
     * =====================================================
     * CLEAR CACHE
     * =====================================================
     */
    public void clear() {

        certificate.set(
            null
        );
    }
}