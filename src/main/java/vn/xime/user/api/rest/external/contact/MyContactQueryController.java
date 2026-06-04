package vn.xime.user.api.rest.external.contact;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.contact.ContactResponse;

import vn.xime.user.application.usecase.contact.GetMyContactsUseCase;


@RestController
@RequestMapping("/api/v1/me/contacts")
@RequiredArgsConstructor
public class MyContactQueryController {

    private final GetMyContactsUseCase getMyContactsUseCase;


    @GetMapping
    public List<ContactResponse> getMyContacts(
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

        return getMyContactsUseCase.execute(
            identityId
        );
    }
}
