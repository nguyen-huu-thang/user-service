package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_users_username",
                        columnNames = {"username"}
                )
        },
        indexes = {
                @Index(name = "idx_users_id", columnList = "id"),
                @Index(name = "idx_users_username", columnList = "username"),
                @Index(name = "idx_users_status", columnList = "status"),
                @Index(name = "idx_users_created_at", columnList = "created_at")
        }
)
public class UserEntity {

    // =========================
    // ID (KSUID - BYTEA)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // BASIC INFO
    // =========================

    // username nullable: register by email/phone may have no username yet.
    // Postgres treats NULLs as distinct, so uk_users_username still allows
    // multiple null-username rows.
    @Column(name = "username", nullable = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    // =========================
    // STATUS
    // =========================

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    // =========================
    // AUDIT
    // =========================

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}