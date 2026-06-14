package vn.xime.user.application.usecase.interest;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.interest.model.Interest;
import vn.xime.user.domain.interest.model.UserInterest;

import vn.xime.user.application.dto.external.interest.MyInterestResponse;
import vn.xime.user.application.mapper.interest.UserInterestMapper;
import vn.xime.user.application.port.out.interest.InterestRepository;
import vn.xime.user.application.port.out.interest.UserInterestRepository;


@Component
@RequiredArgsConstructor
public class AddMyInterestUseCase {

    private final UserInterestRepository userInterestRepository;

    private final InterestRepository interestRepository;

    private final UserInterestMapper mapper;


    @Transactional
    public MyInterestResponse execute(
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
         * VERIFY INTEREST EXISTS
         * =========================
         */

        Id iId = IdService.fromString(
            interestId
        );

        Interest interest =
            interestRepository.findById(iId)
                .orElseThrow(
                    () -> new PublicError(
                        ErrorCode.INTEREST_NOT_FOUND
                    )
                );


        /*
         * =========================
         * CHECK DUPLICATE
         * =========================
         */

        boolean alreadyAdded =
            userInterestRepository
                .findByUserId(userId)
                .stream()
                .anyMatch(ui ->
                    ui.getInterestId().equals(iId)
                );

        if (alreadyAdded) {

            throw new PublicError(
                ErrorCode.INTEREST_ALREADY_ADDED
            );
        }


        /*
         * =========================
         * CREATE USER INTEREST
         * =========================
         */

        UserInterest userInterest =
            new UserInterest(
                IdFactory.generate(),
                userId,
                iId,
                1.0
            );


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserInterest saved =
            userInterestRepository.save(
                userInterest
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponse(
            saved,
            interest
        );
    }
}
