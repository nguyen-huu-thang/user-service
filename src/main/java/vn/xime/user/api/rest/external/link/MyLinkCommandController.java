package vn.xime.user.api.rest.external.link;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.link.AddLinkRequest;
import vn.xime.user.application.dto.external.link.LinkResponse;
import vn.xime.user.application.dto.external.link.UpdateLinkRequest;

import vn.xime.user.application.usecase.link.AddMyLinkUseCase;
import vn.xime.user.application.usecase.link.DeleteMyLinkUseCase;
import vn.xime.user.application.usecase.link.UpdateMyLinkUseCase;


@RestController
@RequestMapping("/api/v1/me/links")
@RequiredArgsConstructor
public class MyLinkCommandController {

    private final AddMyLinkUseCase addMyLinkUseCase;

    private final UpdateMyLinkUseCase updateMyLinkUseCase;

    private final DeleteMyLinkUseCase deleteMyLinkUseCase;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LinkResponse addLink(

        Authentication authentication,

        @Valid
        @RequestBody
        AddLinkRequest request
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

        return addMyLinkUseCase.execute(
            identityId,
            request
        );
    }


    @PatchMapping("/{id}")
    public LinkResponse updateLink(

        Authentication authentication,

        @PathVariable
        String id,

        @Valid
        @RequestBody
        UpdateLinkRequest request
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

        return updateMyLinkUseCase.execute(
            identityId,
            id,
            request
        );
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(

        Authentication authentication,

        @PathVariable
        String id
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

        deleteMyLinkUseCase.execute(
            identityId,
            id
        );
    }
}
