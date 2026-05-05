package vn.xime.user.domain.repository;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserInterest;

import java.util.List;

public interface UserInterestRepository {

    UserInterest save(UserInterest userInterest);

    List<UserInterest> findByUserId(Id userId);

    List<UserInterest> findByInterestId(Id interestId);

    // =========================
    // TOP / WEIGHT
    // =========================

    List<UserInterest> findTopByUserId(Id userId, int limit);

    // =========================
    // DELETE
    // =========================

    void deleteByUserId(Id userId);

    void deleteByUserIdAndInterestId(Id userId, Id interestId);
}