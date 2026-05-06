package vn.xime.user.domain.service;

import vn.xime.user.domain.model.UserProfile;

import java.time.LocalDate;

public class UserProfileValidationService {

    // =========================
    // CREATE VALIDATION
    // =========================

    public void validateNewProfile(
            LocalDate dateOfBirth,
            String bio
    ) {
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("dateOfBirth cannot be in the future");
        }

        if (bio != null && bio.length() > 1000) {
            throw new IllegalArgumentException("bio too long");
        }
    }

    // =========================
    // UPDATE VALIDATION
    // =========================

    public void validateBasicInfoUpdate(
            String fullName,
            String displayName,
            String bio
    ) {
        if (fullName != null && fullName.isBlank()) {
            throw new IllegalArgumentException("fullName must not be blank");
        }

        if (displayName != null && displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        if (bio != null && bio.length() > 1000) {
            throw new IllegalArgumentException("bio too long");
        }
    }

    public void validateAvatar(String avatarUrl) {
        if (avatarUrl != null && avatarUrl.isBlank()) {
            throw new IllegalArgumentException("avatarUrl must not be blank");
        }
    }

    public void validateLocalization(
            String country,
            String language,
            String timezone
    ) {
        if (country != null && country.isBlank()) {
            throw new IllegalArgumentException("country must not be blank");
        }

        if (language != null && language.isBlank()) {
            throw new IllegalArgumentException("language must not be blank");
        }

        if (timezone != null && timezone.isBlank()) {
            throw new IllegalArgumentException("timezone must not be blank");
        }
    }

    // =========================
    // BUSINESS RULE
    // =========================

    public void ensureProfileExists(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("UserProfile is required");
        }
    }

    // =========================
    // OPTIONAL RULES (FUTURE)
    // =========================

    public void validateAgeRestriction(
            LocalDate dateOfBirth,
            int minimumAge
    ) {
        if (dateOfBirth == null) return;

        LocalDate now = LocalDate.now();

        int age = now.getYear() - dateOfBirth.getYear();

        if (age < minimumAge) {
            throw new IllegalStateException("User does not meet minimum age requirement");
        }
    }
}