package vn.xime.user.domain.repository;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.Interest;

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