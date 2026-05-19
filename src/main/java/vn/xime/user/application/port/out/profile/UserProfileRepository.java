package vn.xime.user.application.port.out.profile;

import vn.xime.user.domain.profile.model.UserProfile;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.util.Optional;

public interface UserProfileRepository {

    UserProfile save(UserProfile profile);

    Optional<UserProfile> findByUserId(Id userId);

    boolean existsByUserId(Id userId);

    boolean deleteByUserId(Id userId);
}