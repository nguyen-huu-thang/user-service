package vn.xime.user.domain.repository;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserUsernameHistory;

import java.util.List;

public interface UserUsernameHistoryRepository {

    UserUsernameHistory save(UserUsernameHistory history);

    List<UserUsernameHistory> findByUserId(Id userId);

    void deleteAllByUserId(Id userId);
}