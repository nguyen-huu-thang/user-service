package vn.xime.user.application.mapper.profile;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.profile.model.UserProfile;
import vn.xime.user.application.dto.external.profile.MyProfileResponse;

@Component
public class UserProfileMapper {
    
    public MyProfileResponse toMyProfileResponse(
        UserProfile profile
    ) {

        return new MyProfileResponse(

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
