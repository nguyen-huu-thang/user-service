package vn.xime.user.api.rest.external.interest;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.interest.MyInterestResponse;

import vn.xime.user.application.usecase.interest.GetMyInterestsUseCase;


@RestController
@RequestMapping("/api/v1/me/interests")
@RequiredArgsConstructor
public class MyInterestQueryController {

    private final GetMyInterestsUseCase getMyInterestsUseCase;


    @GetMapping
    public List<MyInterestResponse> getMyInterests(
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

        return getMyInterestsUseCase.execute(
            identityId
        );
    }
}
