package vn.xime.user.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(
        name = "user_profiles",
        indexes = {
                @Index(name = "idx_user_profiles_user_id", columnList = "user_id"),
                @Index(name = "idx_user_profiles_updated_at", columnList = "updated_at")
        }
)
public class UserProfileEntity {

    // =========================
    // PK / FK (USER ID)
    // =========================

    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "BYTEA")
    private byte[] userId;

    // =========================
    // BASIC INFO
    // =========================

    @Column(name = "full_name", length = 255)
    private String fullName;

    @Column(name = "display_name", length = 255)
    private String displayName;

    // =========================
    // PERSONAL INFO
    // =========================

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    // =========================
    // PROFILE
    // =========================

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    // =========================
    // LOCALIZATION
    // =========================

    @Column(name = "country", length = 10)
    private String country;

    @Column(name = "language", length = 10)
    private String language;

    @Column(name = "timezone", length = 50)
    private String timezone;

    // =========================
    // AUDIT
    // =========================

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // =========================
    // GETTER / SETTER
    // =========================

    public byte[] getUserId() {
        return userId;
    }

    public void setUserId(byte[] userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}