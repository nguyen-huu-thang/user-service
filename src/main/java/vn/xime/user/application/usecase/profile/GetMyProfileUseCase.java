package vn.xime.user.application.usecase.profile;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.profile.model.UserProfile;

import vn.xime.user.application.dto.external.profile.MyProfileResponse;
import vn.xime.user.application.mapper.profile.UserProfileMapper;
import vn.xime.user.application.port.out.profile.UserProfileRepository;


@Component
@RequiredArgsConstructor
public class GetMyProfileUseCase {

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper mapper;


    @Transactional(readOnly = true)
    public MyProfileResponse execute(
        String identifier
    ) {

        /*
         * =========================
         * USER ID
         * =========================
         */

        Id userId = IdService.fromString(
            identifier
        );


        /*
         * =========================
         * LOAD PROFILE
         * =========================
         */

        UserProfile profile =
            userProfileRepository
                .findByUserId(userId)
                .orElseThrow(
                    () -> new PublicError(
                        ErrorCode.PROFILE_NOT_FOUND
                    )
                );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toMyProfileResponse(
            profile
        );
    }
}