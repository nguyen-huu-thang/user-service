package vn.xime.user.application.usecase.interest;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.port.out.interest.UserInterestRepository;


@Component
@RequiredArgsConstructor
public class DeleteMyInterestUseCase {

    private final UserInterestRepository userInterestRepository;


    @Transactional
    public void execute(
        String identifier,
        String interestId
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
         * INTEREST ID
         * =========================
         */

        Id iId = IdService.fromString(
            interestId
        );


        /*
         * =========================
         * DELETE
         * =========================
         */

        userInterestRepository
            .deleteByUserIdAndInterestId(
                userId,
                iId
            );
    }
}
