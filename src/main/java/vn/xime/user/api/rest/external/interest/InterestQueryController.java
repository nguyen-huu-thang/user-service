package vn.xime.user.api.rest.external.interest;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.interest.InterestResponse;

import vn.xime.user.application.usecase.interest.GetAllInterestsUseCase;


@RestController
@RequestMapping("/api/v1/interests")
@RequiredArgsConstructor
public class InterestQueryController {

    private final GetAllInterestsUseCase getAllInterestsUseCase;


    @GetMapping
    public List<InterestResponse> getAllInterests() {

        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return getAllInterestsUseCase.execute();
    }
}
