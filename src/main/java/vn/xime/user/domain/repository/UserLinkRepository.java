package vn.xime.user.domain.repository;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.LinkType;
import vn.xime.user.domain.model.UserLink;

import java.util.List;
import java.util.Optional;

public interface UserLinkRepository {

    UserLink save(UserLink link);

    Optional<UserLink> findById(Id id);

    List<UserLink> findByUserId(Id userId);

    List<UserLink> findByUserIdAndType(Id userId, LinkType type);

    boolean deleteById(Id id);

    void deleteAllByUserId(Id userId);
}