package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserUsernameHistory;
import vn.xime.user.domain.repository.UserUsernameHistoryRepository;
import vn.xime.user.infrastructure.persistence.mapper.UserUsernameHistoryMapper;

import java.util.List;

@Repository
public class UserUsernameHistoryRepositoryImpl implements UserUsernameHistoryRepository {

    private final JpaUserUsernameHistoryRepository repo;

    public UserUsernameHistoryRepositoryImpl(JpaUserUsernameHistoryRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserUsernameHistory save(UserUsernameHistory history) {
        var entity = UserUsernameHistoryMapper.toEntity(history);
        var saved = repo.save(entity);
        return UserUsernameHistoryMapper.toDomain(saved);
    }

    @Override
    public List<UserUsernameHistory> findByUserId(Id userId) {
        return repo.findByUserId(userId.toBytes())
                .stream()
                .map(UserUsernameHistoryMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteAllByUserId(Id userId) {
        repo.deleteByUserId(userId.toBytes());
    }
}