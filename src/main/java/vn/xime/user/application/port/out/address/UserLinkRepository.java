package vn.xime.user.application.port.out.address;

import vn.xime.user.domain.contact.model.LinkType;
import vn.xime.user.domain.contact.model.UserLink;
import vn.xime.user.domain.sharedkernel.model.Id;

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