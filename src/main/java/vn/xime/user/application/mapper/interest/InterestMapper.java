package vn.xime.user.application.mapper.interest;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.interest.model.Interest;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.dto.external.interest.InterestResponse;

@Component
public class InterestMapper {

    public InterestResponse toResponse(
        Interest interest
    ) {

        return new InterestResponse(

            IdService.toString(interest.getId()),

            interest.getName()
        );
    }

    public List<InterestResponse> toResponseList(
        List<Interest> interests
    ) {

        return interests.stream()
            .map(this::toResponse)
            .toList();
    }
}
