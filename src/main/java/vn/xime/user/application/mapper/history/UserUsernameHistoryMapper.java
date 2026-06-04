package vn.xime.user.application.mapper.history;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.user.model.UserUsernameHistory;

import vn.xime.user.application.dto.external.history.UsernameHistoryResponse;

@Component
public class UserUsernameHistoryMapper {

    public UsernameHistoryResponse toResponse(
        UserUsernameHistory history
    ) {

        return new UsernameHistoryResponse(

            history.getOldUsername(),

            history.getChangedAt()
        );
    }

    public List<UsernameHistoryResponse> toResponseList(
        List<UserUsernameHistory> histories
    ) {

        return histories.stream()
            .map(this::toResponse)
            .toList();
    }
}
