package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_links",
        indexes = {
                @Index(name = "idx_user_links_id", columnList = "id"),
                @Index(name = "idx_user_links_user_id", columnList = "user_id"),
                @Index(name = "idx_user_links_type", columnList = "type"),
                @Index(name = "idx_user_links_user_type", columnList = "user_id,type")
        }
)
public class UserLinkEntity {

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
    // LINK INFO
    // =========================

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT")
    private String url;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}