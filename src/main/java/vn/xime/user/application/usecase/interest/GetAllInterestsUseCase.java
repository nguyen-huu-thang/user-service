package vn.xime.user.application.usecase.interest;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.interest.model.Interest;

import vn.xime.user.application.dto.external.interest.InterestResponse;
import vn.xime.user.application.mapper.interest.InterestMapper;
import vn.xime.user.application.port.out.interest.InterestRepository;


@Component
@RequiredArgsConstructor
public class GetAllInterestsUseCase {

    private final InterestRepository interestRepository;

    private final InterestMapper mapper;


    @Transactional(readOnly = true)
    public List<InterestResponse> execute() {

        /*
         * =========================
         * LOAD ALL INTERESTS
         * =========================
         */

        List<Interest> interests =
            interestRepository.findAll();


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponseList(
            interests
        );
    }
}
