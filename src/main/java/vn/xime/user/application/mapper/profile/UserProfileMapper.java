package vn.xime.user.application.mapper.profile;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.profile.model.UserProfile;
import vn.xime.user.application.dto.external.profile.CreateMyProfileResponse;

@Component
public class UserProfileMapper {
    
    public CreateMyProfileResponse toCreateMyProfileResponse(
        UserProfile profile
    ) {

        return new CreateMyProfileResponse(

            IdService.toString(profile.getUserId()),

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
