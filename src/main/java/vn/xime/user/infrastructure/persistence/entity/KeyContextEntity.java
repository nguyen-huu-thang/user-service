package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;


/**
 * =========================================================
 * KEY CONTEXT ENTITY
 * =========================================================
 *
 * Public verification key metadata
 * synchronized from trust-service.
 *
 * User-service only stores:
 *
 * - public verification keys
 *
 * KHÔNG:
 *
 * - private signing key
 * - signing lifecycle
 * - signing ownership
 *
 * Trust-service remains the single source of truth.
 *
 * =========================================================
 */
@Entity
@Table(
    name = "key_contexts",

    indexes = {

        // =================================================
        // KEY LOOKUP
        // =================================================

        @Index(
            name = "idx_key_contexts_key_id",
            columnList = "key_id"
        ),

        @Index(
            name = "idx_key_contexts_activate_at",
            columnList = "activate_at"
        ),

        @Index(
            name = "idx_key_contexts_expires_at",
            columnList = "expires_at"
        ),

        // =================================================
        // FILTERING
        // =================================================

        @Index(
            name = "idx_key_contexts_key_type",
            columnList = "key_type"
        ),

        @Index(
            name = "idx_key_contexts_verifier_id",
            columnList = "verifier_id"
        )
    }
)
public class KeyContextEntity {

    // =====================================================
    // KEY IDENTITY
    // =====================================================

    @Id
    @Column(
        name = "key_id",
        nullable = false,
        length = 100
    )
    private String keyId;


    @Column(
        name = "key_type",
        nullable = false,
        length = 50
    )
    private String keyType;


    @Column(
        name = "key_spec",
        nullable = false,
        length = 100
    )
    private String keySpec;


    /**
     * Service dùng public key để verify.
     */
    @Column(
        name = "verifier_id",
        nullable = false,
        length = 100
    )
    private String verifierId;


    // =====================================================
    // KEY MATERIAL
    // =====================================================

    /**
     * PEM encoded public key.
     */
    @Column(
        name = "public_key",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String publicKey;


    // =====================================================
    // LIFECYCLE
    // =====================================================

    @Column(
        name = "activate_at",
        nullable = false
    )
    private Instant activateAt;


    @Column(
        name = "expires_at",
        nullable = false
    )
    private Instant expiresAt;


    // =====================================================
    // GETTER / SETTER
    // =====================================================

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(
        String keyId
    ) {
        this.keyId = keyId;
    }


    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(
        String keyType
    ) {
        this.keyType = keyType;
    }


    public String getKeySpec() {
        return keySpec;
    }

    public void setKeySpec(
        String keySpec
    ) {
        this.keySpec = keySpec;
    }


    public String getVerifierId() {
        return verifierId;
    }

    public void setVerifierId(
        String verifierId
    ) {
        this.verifierId = verifierId;
    }


    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(
        String publicKey
    ) {
        this.publicKey = publicKey;
    }


    public Instant getActivateAt() {
        return activateAt;
    }

    public void setActivateAt(
        Instant activateAt
    ) {
        this.activateAt = activateAt;
    }


    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(
        Instant expiresAt
    ) {
        this.expiresAt = expiresAt;
    }
}