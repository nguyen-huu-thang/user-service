package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.stereotype.Repository;
import vn.xime.user.domain.model.AddressType;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserAddress;
import vn.xime.user.domain.repository.UserAddressRepository;
import vn.xime.user.infrastructure.persistence.mapper.UserAddressMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class UserAddressRepositoryImpl implements UserAddressRepository {

    private final JpaUserAddressRepository repo;

    public UserAddressRepositoryImpl(JpaUserAddressRepository repo) {
        this.repo = repo;
    }

    // =========================
    // SAVE
    // =========================

    @Override
    public UserAddress save(UserAddress address) {
        var entity = UserAddressMapper.toEntity(address);
        var saved = repo.save(entity);
        return UserAddressMapper.toDomain(saved);
    }

    // =========================
    // FIND
    // =========================

    @Override
    public Optional<UserAddress> findById(Id id) {
        return repo.findByIdBytes(id.toBytes())
                .map(UserAddressMapper::toDomain);
    }

    @Override
    public List<UserAddress> findByUserId(Id userId) {
        return repo.findByUserId(userId.toBytes())
                .stream()
                .map(UserAddressMapper::toDomain)
                .toList();
    }

    // =========================
    // TYPE
    // =========================

    @Override
    public List<UserAddress> findByUserIdAndType(Id userId, AddressType type) {
        return repo.findByUserIdAndType(userId.toBytes(), type.name())
                .stream()
                .map(UserAddressMapper::toDomain)
                .toList();
    }

    // =========================
    // TIME RANGE
    // =========================

    @Override
    public List<UserAddress> findCurrentAddresses(Id userId, Instant now) {
        return repo.findCurrentAddresses(userId.toBytes(), now)
                .stream()
                .map(UserAddressMapper::toDomain)
                .toList();
    }

    @Override
    public List<UserAddress> findPastAddresses(Id userId, Instant now) {
        return repo.findPastAddresses(userId.toBytes(), now)
                .stream()
                .map(UserAddressMapper::toDomain)
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
    public void deleteAllByUserId(Id userId) {
        repo.deleteByUserId(userId.toBytes());
    }
}