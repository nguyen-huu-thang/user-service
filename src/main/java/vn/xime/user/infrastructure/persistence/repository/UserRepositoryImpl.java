package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.user.application.port.out.user.UserRepository;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.User;
import vn.xime.user.domain.user.model.UserStatus;
import vn.xime.user.infrastructure.persistence.mapper.UserMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository repo;

    public UserRepositoryImpl(JpaUserRepository repo) {
        this.repo = repo;
    }

    // =========================
    // SAVE
    // =========================

    @Override
    public User save(User user) {
        var entity = UserMapper.toEntity(user);
        var saved = repo.save(entity);
        return UserMapper.toDomain(saved);
    }

    // =========================
    // FIND
    // =========================

    @Override
    public Optional<User> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repo.findByUsername(username)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repo.existsByUsername(username);
    }

    // =========================
    // STATUS
    // =========================

    @Override
    public List<User> findByStatus(String status) {
        return repo.findByStatus(status)
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findActiveUsers() {
        return repo.findByStatus(UserStatus.ACTIVE.name())
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    // =========================
    // CLEANUP
    // =========================

    @Override
    public List<User> findAllNotDeleted() {
        return repo.findByStatusNot(UserStatus.DELETED.name())
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findAllDeleted() {
        return repo.findByStatus(UserStatus.DELETED.name())
                .stream()
                .map(UserMapper::toDomain)
                .toList();
    }

    // =========================
    // DELETE
    // =========================

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
    public void deleteAllByIds(List<Id> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<byte[]> rawIds = ids.stream()
                .map(Id::toBytes)
                .toList();

        repo.deleteByIdIn(rawIds);
    }
}