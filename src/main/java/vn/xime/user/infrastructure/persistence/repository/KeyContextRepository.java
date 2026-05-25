package vn.xime.user.infrastructure.persistence.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import vn.xime.user.domain.authentication.model.KeyContext;

import vn.xime.user.infrastructure.persistence.entity.KeyContextEntity;
import vn.xime.user.infrastructure.persistence.mapper.KeyContextMapper;


/**
 * =========================================================
 * KEY CONTEXT REPOSITORY
 * =========================================================
 *
 * Repository adapter for:
 *
 * - verification key persistence
 * - verification key lookup
 * - verification key cleanup
 *
 * =========================================================
 */
@Repository
public class KeyContextRepository {

    private final JpaKeyContextRepository repository;


    public KeyContextRepository(
        JpaKeyContextRepository repository
    ) {

        this.repository = repository;
    }


    // =====================================================
    // FIND
    // =====================================================

    public Optional<KeyContext> findById(
        String keyId
    ) {

        return repository
            .findById(keyId)
            .map(
                KeyContextMapper::toDomain
            );
    }


    // =====================================================
    // VALID VERIFICATION KEYS
    // =====================================================

    /**
     * Verification keys:
     *
     * - activate_at <= now
     * - expires_at > now
     */
    public List<KeyContext> findValidVerificationKeys(
        Instant now
    ) {

        return repository
            .findValidVerificationKeys(
                now
            )

            .stream()

            .map(
                KeyContextMapper::toDomain
            )

            .toList();
    }


    // =====================================================
    // FILTERING
    // =====================================================

    public List<KeyContext> findByKeyType(
        String keyType
    ) {

        return repository
            .findByKeyType(
                keyType
            )

            .stream()

            .map(
                KeyContextMapper::toDomain
            )

            .toList();
    }


    public List<KeyContext> findByVerifierId(
        String verifierId
    ) {

        return repository
            .findByVerifierId(
                verifierId
            )

            .stream()

            .map(
                KeyContextMapper::toDomain
            )

            .toList();
    }


    // =====================================================
    // SAVE
    // =====================================================

    public KeyContext save(
        KeyContext keyContext
    ) {

        KeyContextEntity saved =

            repository.save(

                KeyContextMapper.toEntity(
                    keyContext
                )
            );

        return KeyContextMapper.toDomain(
            saved
        );
    }


    public List<KeyContext> saveAll(
        List<KeyContext> keyContexts
    ) {

        List<KeyContextEntity> entities =

            keyContexts.stream()

                .map(
                    KeyContextMapper::toEntity
                )

                .toList();


        return repository
            .saveAll(
                entities
            )

            .stream()

            .map(
                KeyContextMapper::toDomain
            )

            .toList();
    }


    // =====================================================
    // DELETE
    // =====================================================

    public void deleteExpiredKeys(
        Instant now
    ) {

        repository.deleteByExpiresAtBefore(
            now
        );
    }
}