package vn.xime.user.domain.user.factory;

import vn.xime.user.domain.profile.model.Gender;
import vn.xime.user.domain.profile.model.UserProfile;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.time.Instant;
import java.time.LocalDate;

public class UserProfileFactory {

    public UserProfile create(
            Id userId,
            String fullName,
            String displayName,
            LocalDate dateOfBirth,
            Gender gender,
            String avatarUrl,
            String bio,
            String country,
            String language,
            String timezone
    ) {
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (bio != null && bio.length() > 1000) {
            throw new IllegalArgumentException("bio too long");
        }

        if (displayName != null && displayName.isBlank()) {
            throw new IllegalArgumentException("displayName must not be blank");
        }

        if (fullName != null && fullName.isBlank()) {
            throw new IllegalArgumentException("fullName must not be blank");
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        Instant now = Instant.now();

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
}