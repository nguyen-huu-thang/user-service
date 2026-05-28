package vn.xime.user.integration.trust.model;


/**
 * =========================================================
 * ROOT CERTIFICATE RESPONSE
 * =========================================================
 *
 * Transport response object.
 *
 * Used by:
 *
 * - bootstrap trust store
 * - root CA synchronization
 * - trust initialization
 *
 * =========================================================
 */
public record RootCertificate(

    String rootCert,

    String fingerprint,

    long expiresAt
) {
}