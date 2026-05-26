package vn.xime.user.api.rest.external.profile;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.profile.CreateMyProfileRequest;

import vn.xime.user.application.dto.external.profile.MyProfileResponse;

import vn.xime.user.application.dto.external.profile.UpdateMyProfileAvatarRequest;
import vn.xime.user.application.dto.external.profile.UpdateMyProfileLocalizationRequest;
import vn.xime.user.application.dto.external.profile.UpdateMyProfilePersonalInfoRequest;

import vn.xime.user.application.usecase.profile.CreateMyProfileUseCase;

import vn.xime.user.application.usecase.profile.DeleteMyProfileAvatarUseCase;

import vn.xime.user.application.usecase.profile.UpdateMyProfileAvatarUseCase;
import vn.xime.user.application.usecase.profile.UpdateMyProfileLocalizationUseCase;
import vn.xime.user.application.usecase.profile.UpdateMyProfilePersonalInfoUseCase;


@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
public class MyProfileCommandController {

    private final CreateMyProfileUseCase createMyProfileUseCase;

    private final UpdateMyProfilePersonalInfoUseCase
        updateMyProfilePersonalInfoUseCase;

    private final UpdateMyProfileAvatarUseCase
        updateMyProfileAvatarUseCase;

    private final DeleteMyProfileAvatarUseCase
        deleteMyProfileAvatarUseCase;

    private final UpdateMyProfileLocalizationUseCase
        updateMyProfileLocalizationUseCase;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MyProfileResponse createProfile(

        Authentication authentication,

        @Valid
        @RequestBody
        CreateMyProfileRequest request
    ) {

        /*
         * =========================
         * AUTHENTICATED IDENTITY
         * =========================
         */

        String identityId = authentication.getName();


        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return createMyProfileUseCase.execute(
            identityId,
            request
        );
    }


    @PatchMapping("/personal-info")
    public MyProfileResponse updatePersonalInfo(

        Authentication authentication,

        @Valid
        @RequestBody
        UpdateMyProfilePersonalInfoRequest request
    ) {

        /*
         * =========================
         * AUTHENTICATED IDENTITY
         * =========================
         */

        String identityId = authentication.getName();


        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return updateMyProfilePersonalInfoUseCase.execute(
            identityId,
            request
        );
    }


    @PatchMapping("/avatar")
    public MyProfileResponse updateAvatar(

        Authentication authentication,

        @Valid
        @RequestBody
        UpdateMyProfileAvatarRequest request
    ) {

        /*
         * =========================
         * AUTHENTICATED IDENTITY
         * =========================
         */

        String identityId = authentication.getName();


        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return updateMyProfileAvatarUseCase.execute(
            identityId,
            request
        );
    }


    @DeleteMapping("/avatar")
    public MyProfileResponse deleteAvatar(
        Authentication authentication
    ) {

        /*
         * =========================
         * AUTHENTICATED IDENTITY
         * =========================
         */

        String identityId = authentication.getName();


        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return deleteMyProfileAvatarUseCase.execute(
            identityId
        );
    }


    @PatchMapping("/localization")
    public MyProfileResponse updateLocalization(

        Authentication authentication,

        @Valid
        @RequestBody
        UpdateMyProfileLocalizationRequest request
    ) {

        /*
         * =========================
         * AUTHENTICATED IDENTITY
         * =========================
         */

        String identityId = authentication.getName();


        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return updateMyProfileLocalizationUseCase.execute(
            identityId,
            request
        );
    }
}