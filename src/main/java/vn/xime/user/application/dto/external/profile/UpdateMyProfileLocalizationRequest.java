package vn.xime.user.application.dto.external.profile;

public record UpdateMyProfileLocalizationRequest(

    String country,

    String language,

    String timezone
) {
}