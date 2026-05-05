package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.UserInterestEntity;

import java.util.List;

public interface JpaUserInterestRepository extends JpaRepository<UserInterestEntity, byte[]> {

    List<UserInterestEntity> findByUserId(byte[] userId);

    List<UserInterestEntity> findByInterestId(byte[] interestId);

    // =========================
    // TOP (ORDER BY weight DESC)
    // =========================

    @Query("""
        SELECT ui FROM UserInterestEntity ui
        WHERE ui.userId = :userId
        ORDER BY ui.weight DESC
    """)
    List<UserInterestEntity> findTopByUserId(
            @Param("userId") byte[] userId
    );

    // =========================
    // DELETE
    // =========================

    void deleteByUserId(byte[] userId);

    void deleteByUserIdAndInterestId(byte[] userId, byte[] interestId);
}