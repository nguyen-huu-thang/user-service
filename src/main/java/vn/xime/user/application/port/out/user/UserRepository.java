package vn.xime.user.application.port.out.user;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(Id id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    // =========================
    // FILTER BY STATUS
    // =========================

    List<User> findByStatus(String status);

    List<User> findActiveUsers();

    // =========================
    // CLEANUP
    // =========================

    List<User> findAllNotDeleted();

    List<User> findAllDeleted();

    // =========================
    // DELETE
    // =========================

    boolean deleteById(Id id);

    void deleteAllByIds(List<Id> ids);
}