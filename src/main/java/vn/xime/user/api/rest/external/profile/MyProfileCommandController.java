package vn.xime.user.api.rest.external.profile;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;

import vn.xime.user.application.dto.external.profile.CreateMyProfileRequest;
import vn.xime.user.application.dto.external.profile.CreateMyProfileResponse;

import vn.xime.user.application.service.authentication.VerifyAccessToken;
import vn.xime.user.application.usecase.profile.CreateMyProfileUseCase;


@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
public class MyProfileCommandController {

    private final VerifyAccessToken verifyAccessToken;

    private final CreateMyProfileUseCase createMyProfileUseCase;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMyProfileResponse createProfile(

            @RequestHeader("Authorization")
            String authorization,

            @Valid
            @RequestBody
            CreateMyProfileRequest request
    ) {

        /*
         * =========================
         * EXTRACT TOKEN
         * =========================
         */

        String accessToken =
            authorization.replace(
                "Bearer ",
                ""
            );


        /*
         * =========================
         * VERIFY JWT
         * =========================
         */

        String identityId;

        try {

            identityId =
                verifyAccessToken.execute(
                    accessToken
                );

        } catch (Exception ex) {

            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid access token",
                ex
            );
        }


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