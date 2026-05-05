package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(
        name = "user_username_history",
        indexes = {
                @Index(name = "idx_user_username_history_id", columnList = "id"),
                @Index(name = "idx_user_username_history_user_id", columnList = "user_id"),
                @Index(name = "idx_user_username_history_changed_at", columnList = "changed_at"),
                @Index(name = "idx_user_username_history_user_time", columnList = "user_id,changed_at DESC")
        }
)
public class UserUsernameHistoryEntity {

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
    // DATA
    // =========================

    @Column(name = "old_username", nullable = false, length = 100)
    private String oldUsername;

    // =========================
    // AUDIT
    // =========================

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

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

    public String getOldUsername() {
        return oldUsername;
    }

    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }
}