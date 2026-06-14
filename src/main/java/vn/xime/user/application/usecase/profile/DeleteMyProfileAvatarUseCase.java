package vn.xime.user.application.usecase.profile;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.sharedkernel.error.ErrorCode;
import vn.xime.user.domain.sharedkernel.error.PublicError;
import vn.xime.user.domain.profile.model.UserProfile;

import vn.xime.user.application.dto.external.profile.MyProfileResponse;

import vn.xime.user.application.mapper.profile.UserProfileMapper;

import vn.xime.user.application.port.out.profile.UserProfileRepository;


@Component
@RequiredArgsConstructor
public class DeleteMyProfileAvatarUseCase {

    private final UserProfileRepository userProfileRepository;

    private final UserProfileMapper mapper;


    @Transactional
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
         * DELETE AVATAR
         * =========================
         */

        Instant now = Instant.now();


        UserProfile updatedProfile =
            profile.updateAvatar(
                null,
                now
            );


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserProfile savedProfile =
            userProfileRepository.save(
                updatedProfile
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toMyProfileResponse(
            savedProfile
        );
    }
}