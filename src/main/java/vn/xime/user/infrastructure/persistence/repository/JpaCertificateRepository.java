package vn.xime.user.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.xime.user.infrastructure.persistence.entity.CertificateEntity;


public interface JpaCertificateRepository extends JpaRepository<CertificateEntity, String> {

    // =========================
    // ACTIVE CERTIFICATES
    // =========================
    //
    // issued_at <= now
    // &&
    // expires_at > now
    //
    // Used for:
    //
    // - runtime mTLS
    // - certificate preload
    // - bootstrap recovery
    //
    // =========================

    List<CertificateEntity> findByIssuedAtLessThanEqualAndExpiresAtAfter(
        Instant issuedAt,
        Instant expiresAt
    );

    // =========================
    // LATEST CERTIFICATE
    // =========================
    //
    // Used as primary runtime lookup.
    //
    // Sorting:
    //
    // 1. issuedAt DESC
    // 2. certificateId DESC
    //
    // certificateId is time-sortable,
    // so second ordering helps ensure:
    //
    // - deterministic selection
    // - stable ordering
    // - safer same-timestamp handling
    //
    // =========================

    Optional<CertificateEntity> findFirstByOrderByIssuedAtDescCertificateIdDesc();

    // =========================
    // LOOKUP
    // =========================

    Optional<CertificateEntity> findByRefreshTokenId(String refreshTokenId);

    // =========================
    // CLEANUP
    // =========================

    void deleteByExpiresAtBefore(Instant now);

    // =========================
    // DEFAULT HELPERS
    // =========================

    default List<CertificateEntity> findActiveCertificates( Instant now) {

        return findByIssuedAtLessThanEqualAndExpiresAtAfter(
            now,
            now
        );
    }
}