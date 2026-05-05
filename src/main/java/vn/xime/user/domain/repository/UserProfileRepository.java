package vn.xime.user.domain.repository;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserProfile;

import java.util.Optional;

public interface UserProfileRepository {

    UserProfile save(UserProfile profile);

    Optional<UserProfile> findByUserId(Id userId);

    boolean existsByUserId(Id userId);

    boolean deleteByUserId(Id userId);
}