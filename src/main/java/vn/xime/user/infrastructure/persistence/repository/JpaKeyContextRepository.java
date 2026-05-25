package vn.xime.user.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.xime.user.infrastructure.persistence.entity.KeyContextEntity;


/**
 * =========================================================
 * JPA KEY CONTEXT REPOSITORY
 * =========================================================
 *
 * Public verification key storage.
 *
 * User-service only stores:
 *
 * - public verification keys
 *
 * =========================================================
 */
public interface JpaKeyContextRepository
    extends JpaRepository<KeyContextEntity, String> {

    // =====================================================
    // VALID VERIFICATION KEYS
    // =====================================================
    //
    // activate_at <= now
    // &&
    // expires_at > now
    //
    // Used for:
    //
    // - JWT verification
    // - verification key preload
    // - runtime verification cache
    //
    // =====================================================

    List<KeyContextEntity>
    findByActivateAtLessThanEqualAndExpiresAtAfter(
        Instant activateAt,
        Instant expiresAt
    );


    // =====================================================
    // CLEANUP
    // =====================================================

    /**
     * Remove expired verification keys.
     */
    void deleteByExpiresAtBefore(
        Instant now
    );


    // =====================================================
    // FILTERING
    // =====================================================

    List<KeyContextEntity> findByKeyType(
        String keyType
    );


    List<KeyContextEntity> findByVerifierId(
        String verifierId
    );


    // =====================================================
    // DEFAULT HELPERS
    // =====================================================

    default List<KeyContextEntity>
    findValidVerificationKeys(
        Instant now
    ) {

        return
            findByActivateAtLessThanEqualAndExpiresAtAfter(
                now,
                now
            );
    }
}