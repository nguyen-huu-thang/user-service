package vn.xime.user.application.dto.external.profile;

import java.time.LocalDate;

import vn.xime.user.domain.profile.model.Gender;

public record UpdateMyProfilePersonalInfoRequest(

    String fullName,

    String displayName,

    LocalDate dateOfBirth,

    Gender gender,

    String bio
) {
}