package vn.xime.user.application.dto.external.profile;

import java.time.Instant;
import java.time.LocalDate;

import vn.xime.user.domain.profile.model.Gender;

public record CreateMyProfileResponse(

    String userId,

    String fullName,

    String displayName,

    String effectiveDisplayName,

    LocalDate dateOfBirth,

    Gender gender,

    String avatarUrl,

    String bio,

    String country,

    String language,

    String timezone,

    boolean hasAvatar,

    Instant updatedAt
) {
}