package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.UserAddressEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface JpaUserAddressRepository extends JpaRepository<UserAddressEntity, byte[]> {

    @Query("SELECT a FROM UserAddressEntity a WHERE a.id = :id")
    Optional<UserAddressEntity> findByIdBytes(@Param("id") byte[] id);

    List<UserAddressEntity> findByUserId(byte[] userId);

    List<UserAddressEntity> findByUserIdAndType(byte[] userId, String type);

    // =========================
    // TIME RANGE
    // =========================

    @Query("""
        SELECT a FROM UserAddressEntity a
        WHERE a.userId = :userId
          AND (a.startDate IS NULL OR a.startDate <= :now)
          AND (a.endDate IS NULL OR a.endDate > :now)
    """)
    List<UserAddressEntity> findCurrentAddresses(
            @Param("userId") byte[] userId,
            @Param("now") Instant now
    );

    @Query("""
        SELECT a FROM UserAddressEntity a
        WHERE a.userId = :userId
          AND a.endDate IS NOT NULL
          AND a.endDate <= :now
    """)
    List<UserAddressEntity> findPastAddresses(
            @Param("userId") byte[] userId,
            @Param("now") Instant now
    );

    // =========================
    // DELETE
    // =========================

    void deleteByUserId(byte[] userId);
}