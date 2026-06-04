package vn.xime.user.api.rest.external.interest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.interest.MyInterestResponse;

import vn.xime.user.application.usecase.interest.AddMyInterestUseCase;
import vn.xime.user.application.usecase.interest.DeleteMyInterestUseCase;


@RestController
@RequestMapping("/api/v1/me/interests")
@RequiredArgsConstructor
public class MyInterestCommandController {

    private final AddMyInterestUseCase addMyInterestUseCase;

    private final DeleteMyInterestUseCase deleteMyInterestUseCase;


    @PostMapping("/{interestId}")
    @ResponseStatus(HttpStatus.CREATED)
    public MyInterestResponse addInterest(

        Authentication authentication,

        @PathVariable
        String interestId
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

        return addMyInterestUseCase.execute(
            identityId,
            interestId
        );
    }


    @DeleteMapping("/{interestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInterest(

        Authentication authentication,

        @PathVariable
        String interestId
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

        deleteMyInterestUseCase.execute(
            identityId,
            interestId
        );
    }
}
