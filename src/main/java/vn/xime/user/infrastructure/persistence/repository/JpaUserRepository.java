package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity, byte[]> {

    @Query("SELECT u FROM UserEntity u WHERE u.id = :id")
    Optional<UserEntity> findByIdBytes(@Param("id") byte[] id);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    // =========================
    // STATUS
    // =========================

    List<UserEntity> findByStatus(String status);

    List<UserEntity> findByStatusAndStatusNot(String status, String notStatus);

    List<UserEntity> findByStatusNot(String status);

    // =========================
    // BATCH DELETE
    // =========================

    void deleteByIdIn(List<byte[]> ids);
}