package vn.xime.user.application.port.out.address;

import vn.xime.user.domain.address.model.AddressType;
import vn.xime.user.domain.address.model.UserAddress;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserAddressRepository {

    UserAddress save(UserAddress address);

    Optional<UserAddress> findById(Id id);

    List<UserAddress> findByUserId(Id userId);

    // =========================
    // TYPE
    // =========================

    List<UserAddress> findByUserIdAndType(Id userId, AddressType type);

    // =========================
    // TIME RANGE
    // =========================

    List<UserAddress> findCurrentAddresses(Id userId, Instant now);

    List<UserAddress> findPastAddresses(Id userId, Instant now);

    // =========================
    // DELETE
    // =========================

    boolean deleteById(Id id);

    void deleteAllByUserId(Id userId);
}