package vn.xime.user.api.rest.external.contact;

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

import vn.xime.user.application.dto.external.contact.AddContactRequest;
import vn.xime.user.application.dto.external.contact.ContactResponse;

import vn.xime.user.application.usecase.contact.AddMyContactUseCase;
import vn.xime.user.application.usecase.contact.DeleteMyContactUseCase;
import vn.xime.user.application.usecase.contact.SetPrimaryContactUseCase;


@RestController
@RequestMapping("/api/v1/me/contacts")
@RequiredArgsConstructor
public class MyContactCommandController {

    private final AddMyContactUseCase addMyContactUseCase;

    private final DeleteMyContactUseCase deleteMyContactUseCase;

    private final SetPrimaryContactUseCase setPrimaryContactUseCase;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContactResponse addContact(

        Authentication authentication,

        @Valid
        @RequestBody
        AddContactRequest request
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

        return addMyContactUseCase.execute(
            identityId,
            request
        );
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteContact(

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

        deleteMyContactUseCase.execute(
            identityId,
            id
        );
    }


    @PatchMapping("/{id}/primary")
    public ContactResponse setPrimaryContact(

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

        return setPrimaryContactUseCase.execute(
            identityId,
            id
        );
    }
}
