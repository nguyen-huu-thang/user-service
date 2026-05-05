package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "user_contacts",
        indexes = {
                @Index(name = "idx_user_contacts_id", columnList = "id"),
                @Index(name = "idx_user_contacts_user_id", columnList = "user_id"),
                @Index(name = "idx_user_contacts_type", columnList = "type"),
                @Index(name = "idx_user_contacts_user_type", columnList = "user_id,type"),
                @Index(name = "idx_user_contacts_primary", columnList = "user_id,is_primary")
        }
)
public class UserContactEntity {

    // =========================
    // ID (KSUID - BYTEA)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // RELATIONSHIP
    // =========================

    @Column(name = "user_id", nullable = false, columnDefinition = "BYTEA")
    private byte[] userId;

    // =========================
    // CONTACT INFO
    // =========================

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    // =========================
    // FLAGS
    // =========================

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary = false;

    // =========================
    // AUDIT
    // =========================

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getIsVerified() {
        return isVerified;
    }

    public void setIsVerified(Boolean verified) {
        isVerified = verified;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        isPrimary = primary;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}