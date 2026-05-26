package vn.xime.user.api.rest.external.profile;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.profile.MyProfileResponse;

import vn.xime.user.application.usecase.profile.GetMyProfileUseCase;


@RestController
@RequestMapping("/api/v1/me/profile")
@RequiredArgsConstructor
public class MyProfileQueryController {

    private final GetMyProfileUseCase getMyProfileUseCase;


    @GetMapping
    public MyProfileResponse getMyProfile(
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

        return getMyProfileUseCase.execute(
            identityId
        );
    }
}