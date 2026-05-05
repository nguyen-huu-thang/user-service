package vn.xime.user.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.xime.user.infrastructure.persistence.entity.UserUsernameHistoryEntity;

import java.util.List;

public interface JpaUserUsernameHistoryRepository extends JpaRepository<UserUsernameHistoryEntity, byte[]> {

    List<UserUsernameHistoryEntity> findByUserId(byte[] userId);

    void deleteByUserId(byte[] userId);
}