package vn.xime.user.application.dto.external.profile;

import java.time.LocalDate;

import vn.xime.user.domain.profile.model.Gender;

public record CreateMyProfileRequest(

    String fullName,

    String displayName,

    LocalDate dateOfBirth,

    Gender gender,

    String bio,

    String country,

    String language,

    String timezone
) {
}