package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.UserProfileEntity;

import java.util.Optional;

public interface JpaUserProfileRepository extends JpaRepository<UserProfileEntity, byte[]> {

    @Query("SELECT p FROM UserProfileEntity p WHERE p.userId = :userId")
    Optional<UserProfileEntity> findByUserIdBytes(@Param("userId") byte[] userId);

    boolean existsByUserId(byte[] userId);

    void deleteByUserId(byte[] userId);
}