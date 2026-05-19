package vn.xime.user.application.port.out.interest;

import vn.xime.user.domain.interest.model.Interest;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.util.List;
import java.util.Optional;

public interface InterestRepository {

    Interest save(Interest interest);

    Optional<Interest> findById(Id id);

    Optional<Interest> findByName(String name);

    List<Interest> findAll();

    boolean existsByName(String name);

    boolean deleteById(Id id);
}