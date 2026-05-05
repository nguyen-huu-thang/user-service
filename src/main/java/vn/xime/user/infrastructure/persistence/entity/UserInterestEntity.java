package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_interests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_interests_user_interest",
                        columnNames = {"user_id", "interest_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_interests_user_id", columnList = "user_id"),
                @Index(name = "idx_user_interests_interest_id", columnList = "interest_id"),
                @Index(name = "idx_user_interests_weight", columnList = "weight"),
                @Index(name = "idx_user_interests_user_weight", columnList = "user_id,weight DESC")
        }
)
public class UserInterestEntity {

    // =========================
    // COMPOSITE KEY (USER + INTEREST)
    // =========================

    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BYTEA")
    private byte[] userId;

    @Id
    @Column(name = "interest_id", nullable = false, columnDefinition = "BYTEA")
    private byte[] interestId;

    // =========================
    // DATA
    // =========================

    @Column(name = "weight", nullable = false)
    private Double weight;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

    public byte[] getInterestId() {
        return interestId;
    }

    public void setInterestId(byte[] interestId) {
        this.interestId = interestId;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}