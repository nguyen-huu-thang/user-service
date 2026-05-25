package vn.xime.user.integration.trust.key;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.authentication.model.KeyContext;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.port.out.integration.ResolveVerificationKey;


/**
 * =========================================================
 * VERIFICATION KEY RESOLVER
 * =========================================================
 *
 * Runtime in-memory verification key cache.
 *
 * Responsibilities:
 *
 * - cache verification keys in RAM
 * - provide fast runtime key lookup
 * - expose cache update methods
 * - cleanup expired verification keys
 *
 * KHÔNG:
 *
 * - grpc communication
 * - database persistence
 * - scheduler logic
 * - JWT verification logic
 * - trust-service orchestration
 *
 * Background synchronization is handled externally.
 *
 * =========================================================
 */
@Component
public class VerificationKeyResolver implements ResolveVerificationKey {

    /**
     * =====================================================
     * VERIFICATION KEY CACHE
     * =====================================================
     *
     * key:
     * JWT key id (kid)
     *
     * =====================================================
     */
    private final Map<String, KeyContext>
        verificationKeys =

            new ConcurrentHashMap<>();


    /**
     * =====================================================
     * RESOLVE VERIFICATION KEY
     * =====================================================
     *
     * Resolve verification key by JWT key id.
     *
     * =====================================================
     */
    public Optional<KeyContext> resolve(
        Id keyId
    ) {

        if (keyId == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(

            verificationKeys.get(

                IdService.toString(
                    keyId
                )
            )
        );
    }


    /**
     * =====================================================
     * UPDATE VERIFICATION KEYS
     * =====================================================
     *
     * Used by:
     *
     * - startup preload
     * - background synchronization
     * - trust refresh flow
     *
     * =====================================================
     */
    public void update(
        List<KeyContext> keyContexts
    ) {

        for (KeyContext keyContext : keyContexts) {

            verificationKeys.put(

                keyContext.getKeyId(),

                keyContext
            );
        }
    }


    /**
     * =====================================================
     * REMOVE VERIFICATION KEY
     * =====================================================
     */
    public void remove(
        String keyId
    ) {

        verificationKeys.remove(
            keyId
        );
    }


    /**
     * =====================================================
     * CLEAN EXPIRED VERIFICATION KEYS
     * =====================================================
     *
     * Remove expired keys from runtime cache.
     *
     * =====================================================
     */
    public void cleanExpiredKeys() {

        Instant now =
            Instant.now();


        verificationKeys.entrySet()
            .removeIf(entry -> {

                KeyContext keyContext =
                    entry.getValue();

                Instant expiresAt =
                    keyContext.getExpiresAt();

                return expiresAt != null
                    && expiresAt.isBefore(
                        now
                    );
            });
    }


    /**
     * =====================================================
     * CLEAR ALL CACHE
     * =====================================================
     */
    public void clear() {

        verificationKeys.clear();
    }


    /**
     * =====================================================
     * CACHE SIZE
     * =====================================================
     */
    public int size() {

        return verificationKeys.size();
    }
}