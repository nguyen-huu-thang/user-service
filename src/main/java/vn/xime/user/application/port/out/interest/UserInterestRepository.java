package vn.xime.user.application.port.out.interest;

import vn.xime.user.domain.interest.model.UserInterest;
import vn.xime.user.domain.sharedkernel.model.Id;

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