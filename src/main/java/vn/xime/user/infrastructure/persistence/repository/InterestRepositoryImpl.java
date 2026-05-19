package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;

import vn.xime.user.application.port.out.interest.InterestRepository;
import vn.xime.user.domain.interest.model.Interest;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.infrastructure.persistence.mapper.InterestMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class InterestRepositoryImpl implements InterestRepository {

    private final JpaInterestRepository repo;

    public InterestRepositoryImpl(JpaInterestRepository repo) {
        this.repo = repo;
    }

    @Override
    public Interest save(Interest interest) {
        var entity = InterestMapper.toEntity(interest);
        var saved = repo.save(entity);
        return InterestMapper.toDomain(saved);
    }

    @Override
    public Optional<Interest> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(InterestMapper::toDomain);
    }

    @Override
    public Optional<Interest> findByName(String name) {
        return repo.findByName(name)
                .map(InterestMapper::toDomain);
    }

    @Override
    public List<Interest> findAll() {
        return repo.findAll()
                .stream()
                .map(InterestMapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByName(String name) {
        return repo.existsByName(name);
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
}