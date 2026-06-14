package vn.xime.user.application.usecase.profile;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.profile.model.UserProfile;

import vn.xime.user.application.dto.external.profile.CreateMyProfileRequest;
import vn.xime.user.application.dto.external.profile.MyProfileResponse;
import vn.xime.user.application.mapper.profile.UserProfileMapper;
import vn.xime.user.application.port.out.profile.UserProfileRepository;


@Component
@RequiredArgsConstructor
public class CreateMyProfileUseCase {

    private final UserProfileRepository userProfileRepository;
    
    private final UserProfileMapper mapper;


    @Transactional
    public MyProfileResponse execute(
        String identifier,
        CreateMyProfileRequest request
    ) {

        /*
         * =========================
         * USER ID
         * =========================
         */

        Id userId = IdService.fromString(identifier);


        /*
         * =========================
         * DUPLICATE CHECK
         * =========================
         */

        if (
            userProfileRepository.existsByUserId(
                userId
            )
        ) {

            throw new PublicError(
                ErrorCode.PROFILE_ALREADY_EXISTS
            );
        }


        /*
         * =========================
         * CREATE PROFILE
         * =========================
         */

        Instant now = Instant.now();


        UserProfile profile =

            new UserProfile(
                userId,

                request.fullName(),

                request.displayName(),

                request.dateOfBirth(),

                request.gender(),

                null,

                request.bio(),

                request.country(),

                request.language(),

                request.timezone(),

                now
            );


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserProfile savedProfile = userProfileRepository.save(
            profile
        );

        return mapper.toMyProfileResponse(savedProfile);
    }
}