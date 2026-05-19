package vn.xime.user.application.port.out.user;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.UserUsernameHistory;

import java.util.List;

public interface UserUsernameHistoryRepository {

    UserUsernameHistory save(UserUsernameHistory history);

    List<UserUsernameHistory> findByUserId(Id userId);

    void deleteAllByUserId(Id userId);
}