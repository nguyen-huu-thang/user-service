package vn.xime.user.domain.authentication.model;

import java.time.Instant;

/**
 * =========================================================
 * KEY CONTEXT
 * =========================================================
 *
 * Verification key metadata used for:
 *
 * - JWT verification
 * - internal token verification
 * - trust validation
 *
 * This model contains:
 *
 * - public verification key only
 *
 * KHÔNG:
 *
 * - private signing key
 * - signing lifecycle logic
 * - key rotation logic
 *
 * Trust-service is the source of truth.
 *
 * =========================================================
 */
public class KeyContext {

    /**
     * =====================================================
     * KEY ID (kid)
     * =====================================================
     */
    private final String keyId;


    /**
     * =====================================================
     * KEY TYPE
     * =====================================================
     *
     * Examples:
     *
     * - RSA
     * - EC
     *
     * =====================================================
     */
    private final String keyType;


    /**
     * =====================================================
     * KEY SPEC
     * =====================================================
     *
     * Examples:
     *
     * RSA:
     * - 2048
     * - 4096
     *
     * EC:
     * - P-256
     * - P-384
     *
     * =====================================================
     */
    private final String keySpec;


    /**
     * =====================================================
     * VERIFIER SERVICE ID
     * =====================================================
     */
    private final String verifierId;


    /**
     * =====================================================
     * PUBLIC KEY (PEM)
     * =====================================================
     */
    private final String publicKey;


    /**
     * =====================================================
     * ACTIVATE TIME
     * =====================================================
     */
    private final Instant activateAt;


    /**
     * =====================================================
     * EXPIRE TIME
     * =====================================================
     */
    private final Instant expiresAt;


    public KeyContext(
        String keyId,
        String keyType,
        String keySpec,
        String verifierId,
        String publicKey,
        Instant activateAt,
        Instant expiresAt
    ) {

        this.keyId = keyId;
        this.keyType = keyType;
        this.keySpec = keySpec;
        this.verifierId = verifierId;
        this.publicKey = publicKey;
        this.activateAt = activateAt;
        this.expiresAt = expiresAt;
    }


    public String getKeyId() {
        return keyId;
    }


    public String getKeyType() {
        return keyType;
    }


    public String getKeySpec() {
        return keySpec;
    }


    public String getVerifierId() {
        return verifierId;
    }


    public String getPublicKey() {
        return publicKey;
    }


    public Instant getActivateAt() {
        return activateAt;
    }


    public Instant getExpiresAt() {
        return expiresAt;
    }
}