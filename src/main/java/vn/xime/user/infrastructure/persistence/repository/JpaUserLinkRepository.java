package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.xime.user.infrastructure.persistence.entity.UserLinkEntity;

import java.util.List;
import java.util.Optional;

public interface JpaUserLinkRepository extends JpaRepository<UserLinkEntity, byte[]> {

    @Query("SELECT l FROM UserLinkEntity l WHERE l.id = :id")
    Optional<UserLinkEntity> findByIdBytes(@Param("id") byte[] id);

    List<UserLinkEntity> findByUserId(byte[] userId);

    List<UserLinkEntity> findByUserIdAndType(byte[] userId, String type);

    void deleteByUserId(byte[] userId);
}