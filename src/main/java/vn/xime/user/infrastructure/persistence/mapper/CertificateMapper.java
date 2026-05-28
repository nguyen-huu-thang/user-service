package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.integration.trust.model.Certificate;
import vn.xime.user.infrastructure.persistence.entity.CertificateEntity;


public class CertificateMapper {

    // =========================
    // Entity -> Record
    // =========================

    public static Certificate toRecord(
        CertificateEntity e
    ) {

        if (e == null) {
            throw new IllegalArgumentException(
                "CertificateEntity must not be null"
            );
        }

        requireNonNull(
            e.getRefreshTokenId(),
            "refreshTokenId"
        );

        requireNonNull(
            e.getCertificateId(),
            "certificateId"
        );

        requireNonNull(
            e.getServiceId(),
            "serviceId"
        );

        requireNonNull(
            e.getPublicCertificate(),
            "publicCertificate"
        );

        requireNonNull(
            e.getIssuedAt(),
            "issuedAt"
        );

        requireNonNull(
            e.getExpiresAt(),
            "expiresAt"
        );

        return new Certificate(
            e.getCertificateId(),
            e.getPublicCertificate(),
            e.getPrivateKey(),
            e.getServiceId(),
            e.getRefreshTokenId(),
            e.getRefreshToken(),
            e.getIssuedAt(),
            e.getExpiresAt()
        );
    }

    // =========================
    // Record -> Entity
    // =========================

    public static CertificateEntity toEntity(
        Certificate r
    ) {

        if (r == null) {
            throw new IllegalArgumentException(
                "Certificate must not be null"
            );
        }

        CertificateEntity e =
            new CertificateEntity();

        e.setRefreshTokenId(
            r.refreshTokenId()
        );

        e.setCertificateId(
            r.certificateId()
        );

        e.setServiceId(
            r.serviceId()
        );

        e.setPublicCertificate(
            r.publicCertificate()
        );

        e.setPrivateKey(
            r.privateKey()
        );

        e.setRefreshToken(
            r.refreshToken()
        );

        e.setIssuedAt(
            r.issuedAt()
        );

        e.setExpiresAt(
            r.expiresAt()
        );

        return e;
    }

    // =========================
    // HELPERS
    // =========================

    private static void requireNonNull(
        Object value,
        String field
    ) {

        if (value == null) {
            throw new IllegalStateException(
                field + " must not be null"
            );
        }
    }
}