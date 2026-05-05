package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "interests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_interests_name",
                        columnNames = {"name"}
                )
        },
        indexes = {
                @Index(name = "idx_interests_id", columnList = "id"),
                @Index(name = "idx_interests_name", columnList = "name")
        }
)
public class InterestEntity {

    // =========================
    // ID (KSUID - BYTEA)
    // =========================

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "BYTEA")
    private byte[] id;

    // =========================
    // DATA
    // =========================

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}