package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.user.domain.model.ContactType;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserContact;
import vn.xime.user.domain.repository.UserContactRepository;
import vn.xime.user.infrastructure.persistence.mapper.UserContactMapper;

import java.util.List;
import java.util.Optional;

@Repository
public class UserContactRepositoryImpl implements UserContactRepository {

    private final JpaUserContactRepository repo;

    public UserContactRepositoryImpl(JpaUserContactRepository repo) {
        this.repo = repo;
    }

    // =========================
    // SAVE
    // =========================

    @Override
    public UserContact save(UserContact contact) {
        var entity = UserContactMapper.toEntity(contact);
        var saved = repo.save(entity);
        return UserContactMapper.toDomain(saved);
    }

    // =========================
    // FIND
    // =========================

    @Override
    public Optional<UserContact> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(UserContactMapper::toDomain);
    }

    @Override
    public List<UserContact> findByUserId(Id userId) {
        return repo.findByUserId(userId.toBytes())
                .stream()
                .map(UserContactMapper::toDomain)
                .toList();
    }

    // =========================
    // TYPE
    // =========================

    @Override
    public List<UserContact> findByUserIdAndType(Id userId, ContactType type) {
        return repo.findByUserIdAndType(userId.toBytes(), type.name())
                .stream()
                .map(UserContactMapper::toDomain)
                .toList();
    }

    // =========================
    // VERIFIED
    // =========================

    @Override
    public List<UserContact> findVerifiedContacts(Id userId) {
        return repo.findByUserIdAndIsVerifiedTrue(userId.toBytes())
                .stream()
                .map(UserContactMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserContact> findUnverifiedContacts(Id userId) {
        return repo.findByUserIdAndIsVerifiedFalse(userId.toBytes())
                .stream()
                .map(UserContactMapper::toDomain)
                .toList();
    }

    // =========================
    // PRIMARY
    // =========================

    @Override
    public Optional<UserContact> findPrimaryContact(Id userId, ContactType type) {
        return repo.findByUserIdAndTypeAndIsPrimaryTrue(
                        userId.toBytes(),
                        type.name()
                )
                .map(UserContactMapper::toDomain);
    }

    // =========================
    // VALUE LOOKUP
    // =========================

    @Override
    public Optional<UserContact> findByTypeAndValue(ContactType type, String value) {
        return repo.findByTypeAndValue(type.name(), value)
                .map(UserContactMapper::toDomain);
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
    public void deleteAllByUserId(Id userId) {
        repo.deleteByUserId(userId.toBytes());
    }
}