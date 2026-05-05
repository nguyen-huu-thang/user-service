package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.LinkType;
import vn.xime.user.domain.model.UserLink;
import vn.xime.user.domain.repository.UserLinkRepository;
import vn.xime.user.infrastructure.persistence.mapper.UserLinkMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class UserLinkRepositoryImpl implements UserLinkRepository {

    private final JpaUserLinkRepository repo;

    public UserLinkRepositoryImpl(JpaUserLinkRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserLink save(UserLink link) {
        var entity = UserLinkMapper.toEntity(link);
        var saved = repo.save(entity);
        return UserLinkMapper.toDomain(saved);
    }

    @Override
    public Optional<UserLink> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(UserLinkMapper::toDomain);
    }

    @Override
    public List<UserLink> findByUserId(Id userId) {
        return repo.findByUserId(userId.toBytes())
                .stream()
                .map(UserLinkMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserLink> findByUserIdAndType(Id userId, LinkType type) {
        return repo.findByUserIdAndType(userId.toBytes(), type.name())
                .stream()
                .map(UserLinkMapper::toDomain)
                .toList();
    }

    @Override
    public boolean deleteById(Id id) {
        byte[] rawId = id.toBytes();

        if (!repo.existsById(rawId)) {
            return false;
        }

        repo.deleteById(rawId);
        return true;
    }

    @Override
    public void deleteAllByUserId(Id userId) {
        repo.deleteByUserId(userId.toBytes());
    }
}