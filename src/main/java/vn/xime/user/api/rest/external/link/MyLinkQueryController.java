package vn.xime.user.api.rest.external.link;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.link.LinkResponse;

import vn.xime.user.application.usecase.link.GetMyLinksUseCase;


@RestController
@RequestMapping("/api/v1/me/links")
@RequiredArgsConstructor
public class MyLinkQueryController {

    private final GetMyLinksUseCase getMyLinksUseCase;


    @GetMapping
    public List<LinkResponse> getMyLinks(
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

        return getMyLinksUseCase.execute(
            identityId
        );
    }
}
