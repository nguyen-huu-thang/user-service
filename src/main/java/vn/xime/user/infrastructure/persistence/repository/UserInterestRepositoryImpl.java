package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.user.application.port.out.interest.UserInterestRepository;
import vn.xime.user.domain.interest.model.UserInterest;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.infrastructure.persistence.mapper.UserInterestMapper;

import java.util.List;

@Repository
public class UserInterestRepositoryImpl implements UserInterestRepository {

    private final JpaUserInterestRepository repo;

    public UserInterestRepositoryImpl(JpaUserInterestRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserInterest save(UserInterest userInterest) {
        var entity = UserInterestMapper.toEntity(userInterest);
        var saved = repo.save(entity);
        return UserInterestMapper.toDomain(saved);
    }

    @Override
    public List<UserInterest> findByUserId(Id userId) {
        return repo.findByUserId(userId.toBytes())
                .stream()
                .map(UserInterestMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserInterest> findByInterestId(Id interestId) {
        return repo.findByInterestId(interestId.toBytes())
                .stream()
                .map(UserInterestMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserInterest> findTopByUserId(Id userId, int limit) {
        return repo.findTopByUserId(userId.toBytes())
                .stream()
                .limit(limit)
                .map(UserInterestMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteByUserId(Id userId) {
        repo.deleteByUserId(userId.toBytes());
    }

    @Override
    public void deleteByUserIdAndInterestId(Id userId, Id interestId) {
        repo.deleteByUserIdAndInterestId(
                userId.toBytes(),
                interestId.toBytes()
        );
    }
}