package vn.xime.user.integration.trust.model;

import java.time.Instant;


/**
 * =========================================================
 * ROTATED CERTIFICATE
 * =========================================================
 *
 * Transport response object.
 *
 * Used by:
 *
 * - bootstrap flow
 * - certificate rotation flow
 * - runtime trust update
 *
 * =========================================================
 */
public record Certificate(

    String certificateId,

    String publicCertificate,

    String privateKey,

    String serviceId,

    String refreshTokenId,

    String refreshToken,

    Instant issuedAt,

    Instant expiresAt
) {
}