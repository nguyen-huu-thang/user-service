package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.user.application.port.out.profile.UserProfileRepository;
import vn.xime.user.domain.profile.model.UserProfile;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.infrastructure.persistence.mapper.UserProfileMapper;

import java.util.Optional;

@Repository
public class UserProfileRepositoryImpl implements UserProfileRepository {

    private final JpaUserProfileRepository repo;

    public UserProfileRepositoryImpl(JpaUserProfileRepository repo) {
        this.repo = repo;
    }

    // =========================
    // SAVE
    // =========================

    @Override
    public UserProfile save(UserProfile profile) {
        var entity = UserProfileMapper.toEntity(profile);
        var saved = repo.save(entity);
        return UserProfileMapper.toDomain(saved);
    }

    // =========================
    // FIND
    // =========================

    @Override
    public Optional<UserProfile> findByUserId(Id userId) {
        return repo.findByUserIdBytes(userId.toBytes())
                .map(UserProfileMapper::toDomain);
    }

    @Override
    public boolean existsByUserId(Id userId) {
        return repo.existsByUserId(userId.toBytes());
    }

    // =========================
    // DELETE
    // =========================

    @Override
    public boolean deleteByUserId(Id userId) {
        byte[] rawId = userId.toBytes();

        if (!repo.existsByUserId(rawId)) {
            return false;
        }

        repo.deleteByUserId(rawId);
        return true;
    }
}