package vn.xime.user.api.rest.external.history;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.history.UsernameHistoryResponse;

import vn.xime.user.application.usecase.history.GetMyUsernameHistoryUseCase;


@RestController
@RequestMapping("/api/v1/me/username-history")
@RequiredArgsConstructor
public class MyUsernameHistoryQueryController {

    private final GetMyUsernameHistoryUseCase getMyUsernameHistoryUseCase;


    @GetMapping
    public List<UsernameHistoryResponse> getMyUsernameHistory(
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

        return getMyUsernameHistoryUseCase.execute(
            identityId
        );
    }
}
