package vn.xime.user.application.mapper.interest;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.interest.model.Interest;
import vn.xime.user.domain.interest.model.UserInterest;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.dto.external.interest.MyInterestResponse;

@Component
public class UserInterestMapper {

    public MyInterestResponse toResponse(
        UserInterest userInterest,
        Interest interest
    ) {

        return new MyInterestResponse(

            IdService.toString(userInterest.getInterestId()),

            interest.getName(),

            userInterest.getWeight()
        );
    }

    public List<MyInterestResponse> toResponseList(
        List<UserInterest> userInterests,
        List<Interest> interests
    ) {

        // Build lookup map: interestId bytes → Interest
        var interestMap = interests.stream()
            .collect(
                java.util.stream.Collectors.toMap(
                    i -> IdService.toString(i.getId()),
                    i -> i
                )
            );

        return userInterests.stream()
            .map(ui -> {

                String interestIdStr =
                    IdService.toString(ui.getInterestId());

                Interest interest =
                    interestMap.get(interestIdStr);

                if (interest == null) {
                    throw new IllegalStateException(
                        "interest not found for id: " + interestIdStr
                    );
                }

                return toResponse(ui, interest);
            })
            .toList();
    }
}
