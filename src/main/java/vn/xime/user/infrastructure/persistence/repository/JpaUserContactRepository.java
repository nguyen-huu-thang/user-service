package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.UserContactEntity;

import java.util.List;
import java.util.Optional;

public interface JpaUserContactRepository extends JpaRepository<UserContactEntity, byte[]> {

    @Query("SELECT c FROM UserContactEntity c WHERE c.id = :id")
    Optional<UserContactEntity> findByIdBytes(@Param("id") byte[] id);

    List<UserContactEntity> findByUserId(byte[] userId);

    List<UserContactEntity> findByUserIdAndType(byte[] userId, String type);

    // =========================
    // VERIFIED
    // =========================

    List<UserContactEntity> findByUserIdAndIsVerifiedTrue(byte[] userId);

    List<UserContactEntity> findByUserIdAndIsVerifiedFalse(byte[] userId);

    // =========================
    // PRIMARY
    // =========================

    Optional<UserContactEntity> findByUserIdAndTypeAndIsPrimaryTrue(
            byte[] userId,
            String type
    );

    // =========================
    // VALUE LOOKUP
    // =========================

    Optional<UserContactEntity> findByTypeAndValue(String type, String value);

    // =========================
    // DELETE
    // =========================

    void deleteByUserId(byte[] userId);
}