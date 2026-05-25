package vn.xime.user.integration.trust.key;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import vn.xime.user.domain.authentication.model.KeyContext;

import vn.xime.user.infrastructure.grpc.trust.key.GrpcTrustKeyDistributionClient;
import vn.xime.user.infrastructure.persistence.repository.KeyContextRepository;


/**
 * =========================================================
 * VERIFICATION KEY SYNCHRONIZER
 * =========================================================
 *
 * Responsibilities:
 *
 * - synchronize verification keys from trust-service
 * - persist verification keys locally
 * - update runtime RAM cache
 * - remove expired verification keys
 *
 * KHÔNG:
 *
 * - grpc transport logic
 * - JWT verification logic
 * - scheduler trigger
 * - trust-service orchestration
 *
 * Verification flow keeps:
 *
 * - all active non-expired public keys
 *
 * because older JWTs may still reference
 * older key ids (kid).
 *
 * Background scheduler should call:
 *
 * synchronize()
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class VerificationKeySynchronizer {

    /**
     * =====================================================
     * VERIFIER SERVICE ID
     * =====================================================
     */
    private static final String VERIFIER_SERVICE_ID = "user-service";


    /**
     * =====================================================
     * GRPC TRUST CLIENT
     * =====================================================
     */
    private final GrpcTrustKeyDistributionClient grpcTrustKeyDistributionClient;


    /**
     * =====================================================
     * RUNTIME VERIFICATION CACHE
     * =====================================================
     */
    private final VerificationKeyResolver verificationKeyResolver;


    /**
     * =====================================================
     * DATABASE REPOSITORY
     * =====================================================
     */
    private final KeyContextRepository keyContextRepository;


    /**
     * =====================================================
     * SYNCHRONIZE VERIFICATION KEYS
     * =====================================================
     *
     * Flow:
     *
     * trust-service
     *      ↓
     * local database
     *      ↓
     * runtime RAM cache
     *
     * Fallback:
     *
     * if trust-service unavailable
     * → load valid keys from database
     *
     * =====================================================
     */
    public void synchronize() {

        // =================================================
        // CURRENT TIME
        // =================================================

        Instant now = Instant.now();


        // =================================================
        // VALID VERIFICATION KEYS
        // =================================================

        List<KeyContext> validKeys;

        try {

            // =============================================
            // LOAD FROM TRUST SERVICE
            // =============================================

            List<KeyContext> keys =

                grpcTrustKeyDistributionClient
                    .getPublicKeys(
                        VERIFIER_SERVICE_ID
                    );


            // =============================================
            // SAVE DATABASE
            // =============================================

            keyContextRepository.saveAll(
                keys
            );


            // =============================================
            // FILTER VALID KEYS
            // =============================================

            validKeys =

                keys.stream()

                    .filter(
                        key ->
                            !key.getActivateAt()
                                .isAfter(now)
                    )

                    .filter(
                        key ->
                            key.getExpiresAt()
                                .isAfter(now)
                    )

                    .toList();

        } catch (RuntimeException exception) {

            // =============================================
            // FALLBACK DATABASE
            // =============================================

            validKeys =

                keyContextRepository
                    .findValidVerificationKeys(
                        now
                    );
        }


        // =================================================
        // UPDATE RUNTIME CACHE
        // =================================================

        verificationKeyResolver.update(
            validKeys
        );


        // =================================================
        // CLEAN EXPIRED CACHE
        // =================================================

        verificationKeyResolver
            .cleanExpiredKeys();
    }
}