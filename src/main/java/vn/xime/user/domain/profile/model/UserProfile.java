package vn.xime.user.domain.profile.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import vn.xime.user.domain.sharedkernel.model.Id;

public class UserProfile {

    private final Id userId;

    private final String fullName;
    private final String displayName;

    private final LocalDate dateOfBirth;
    private final Gender gender;

    private final String avatarUrl;
    private final String bio;

    private final String country;
    private final String language;
    private final String timezone;

    private final Instant updatedAt;

    public UserProfile(
            Id userId,
            String fullName,
            String displayName,
            LocalDate dateOfBirth,
            Gender gender,
            String avatarUrl,
            String bio,
            String country,
            String language,
            String timezone,
            Instant updatedAt
    ) {
        this.userId = Objects.requireNonNull(userId);
        this.fullName = fullName;
        this.displayName = displayName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.bio = bio;
        this.country = country;
        this.language = language;
        this.timezone = timezone;
        this.updatedAt = Objects.requireNonNull(updatedAt);

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
        if (displayName != null && displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        if (fullName != null && fullName.isBlank()) {
            throw new IllegalArgumentException("fullName must not be blank");
        }

        if (bio != null && bio.length() > 1000) {
            throw new IllegalArgumentException("bio too long");
        }
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public String getEffectiveDisplayName() {
        if (displayName != null && !displayName.isBlank()) {
            return displayName;
        }
        return fullName;
    }

    public boolean hasAvatar() {
        return avatarUrl != null && !avatarUrl.isBlank();
    }

    // =========================
    // STATE CHANGE
    // =========================

    public UserProfile updateBasicInfo(
            String fullName,
            String displayName,
            LocalDate dateOfBirth,
            Gender gender,
            String bio,
            Instant now
    ) {
        return new UserProfile(
                userId,
                fullName,
                displayName,
                dateOfBirth,
                gender,
                avatarUrl,
                bio,
                country,
                language,
                timezone,
                now
        );
    }

    public UserProfile updateAvatar(String avatarUrl, Instant now) {
        return new UserProfile(
                userId,
                fullName,
                displayName,
                dateOfBirth,
                gender,
                avatarUrl,
                bio,
                country,
                language,
                timezone,
                now
        );
    }

    public UserProfile updateLocalization(
            String country,
            String language,
            String timezone,
            Instant now
    ) {
        return new UserProfile(
                userId,
                fullName,
                displayName,
                dateOfBirth,
                gender,
                avatarUrl,
                bio,
                country,
                language,
                timezone,
                now
        );
    }

    // =========================
    // GETTERS
    // =========================

    public Id getUserId() {
        return userId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getBio() {
        return bio;
    }

    public String getCountry() {
        return country;
    }

    public String getLanguage() {
        return language;
    }

    public String getTimezone() {
        return timezone;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}