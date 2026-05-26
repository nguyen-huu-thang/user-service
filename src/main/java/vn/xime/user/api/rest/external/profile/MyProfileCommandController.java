package vn.xime.user.api.rest.external.profile;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.profile.CreateMyProfileRequest;
import vn.xime.user.application.dto.external.profile.CreateMyProfileResponse;

import vn.xime.user.application.usecase.profile.CreateMyProfileUseCase;


@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
public class MyProfileCommandController {

    private final CreateMyProfileUseCase createMyProfileUseCase;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMyProfileResponse createProfile(

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

        CreateMyProfileResponse profile =

            createMyProfileUseCase.execute(
                identityId,
                request
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return profile;
    }
}