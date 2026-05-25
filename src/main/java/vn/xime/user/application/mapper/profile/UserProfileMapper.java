package vn.xime.user.application.mapper.profile;

import org.springframework.stereotype.Component;

import vn.xime.user.application.dto.external.profile.CreateMyProfileResponse;
import vn.xime.user.domain.profile.model.UserProfile;

@Component
public class UserProfileMapper {
    
    public CreateMyProfileResponse toCreateMyProfileResponse(
        UserProfile profile
    ) {

        return new CreateMyProfileResponse(

            profile.getUserId().toString(),

            profile.getFullName(),

            profile.getDisplayName(),

            profile.getEffectiveDisplayName(),

            profile.getDateOfBirth(),

            profile.getGender(),

            profile.getAvatarUrl(),

            profile.getBio(),

            profile.getCountry(),

            profile.getLanguage(),

            profile.getTimezone(),

            profile.hasAvatar(),

            profile.getUpdatedAt()
        );
    }
}
