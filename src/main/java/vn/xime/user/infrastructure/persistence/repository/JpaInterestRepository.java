package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.InterestEntity;

import java.util.Optional;

public interface JpaInterestRepository extends JpaRepository<InterestEntity, byte[]> {

    @Query("SELECT i FROM InterestEntity i WHERE i.id = :id")
    Optional<InterestEntity> findByIdBytes(@Param("id") byte[] id);

    Optional<InterestEntity> findByName(String name);

    boolean existsByName(String name);
}