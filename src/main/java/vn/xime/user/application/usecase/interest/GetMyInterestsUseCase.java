package vn.xime.user.application.usecase.interest;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.interest.model.Interest;
import vn.xime.user.domain.interest.model.UserInterest;

import vn.xime.user.application.dto.external.interest.MyInterestResponse;
import vn.xime.user.application.mapper.interest.UserInterestMapper;
import vn.xime.user.application.port.out.interest.InterestRepository;
import vn.xime.user.application.port.out.interest.UserInterestRepository;


@Component
@RequiredArgsConstructor
public class GetMyInterestsUseCase {

    private final UserInterestRepository userInterestRepository;

    private final InterestRepository interestRepository;

    private final UserInterestMapper mapper;


    @Transactional(readOnly = true)
    public List<MyInterestResponse> execute(
        String identifier
    ) {

        /*
         * =========================
         * USER ID
         * =========================
         */

        Id userId = IdService.fromString(
            identifier
        );


        /*
         * =========================
         * LOAD USER INTERESTS
         * =========================
         */

        List<UserInterest> userInterests =
            userInterestRepository.findByUserId(
                userId
            );

        if (userInterests.isEmpty()) {
            return List.of();
        }


        /*
         * =========================
         * LOAD INTEREST DETAILS
         * =========================
         */

        List<Interest> interests =
            userInterests.stream()
                .map(ui ->
                    interestRepository
                        .findById(ui.getInterestId())
                        .orElseThrow(
                            () -> new IllegalStateException(
                                "interest not found"
                            )
                        )
                )
                .toList();


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponseList(
            userInterests,
            interests
        );
    }
}
