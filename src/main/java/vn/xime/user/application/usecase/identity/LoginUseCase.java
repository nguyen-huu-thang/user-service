package vn.xime.user.application.usecase.identity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.user.model.User;
import vn.xime.user.domain.user.model.UserStatus;
import vn.xime.user.domain.authentication.model.VerifiedIdentity;
import vn.xime.user.domain.authentication.service.IdentifierNormalizer;

import vn.xime.user.application.service.identity.ResolveUserByIdentifierService;
import vn.xime.user.application.dto.external.authentication.VerifyCredentialRequest;
import vn.xime.user.application.dto.external.authentication.VerifyCredentialResponse;
import vn.xime.user.application.port.out.crypto.PasswordHasher;




/**
 * =========================================================
 * LOGIN USE CASE
 * =========================================================
 *
 * Internal credential verification flow.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * - validate request
 * - normalize identifier
 * - resolve user
 * - verify password
 * - check account state
 * - build verification result
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * Đây KHÔNG phải public login API.
 *
 * Flow:
 *
 * Identity Service
 * ->
 * User Service
 *
 * User Service chỉ verify:
 *
 * - credential
 * - account state
 *
 * KHÔNG:
 *
 * - issue JWT
 * - refresh token
 * - manage session
 *
 * =========================================================
 * CURRENT STAGE
 * =========================================================
 *
 * Hiện tại:
 *
 * - passwordHash vẫn nằm trong User
 * - chưa có credential aggregate
 * - chưa có MFA
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class LoginUseCase {

    /**
     * =====================================================
     * IDENTIFIER NORMALIZER
     * =====================================================
     */
    private final IdentifierNormalizer
        identifierNormalizer;


    /**
     * =====================================================
     * PASSWORD HASHER
     * =====================================================
     */
    private final PasswordHasher
        passwordHasher;


    /**
     * =====================================================
     * RESOLVE USER
     * =====================================================
     *
     * Resolve:
     *
     * - username owner
     * - email owner
     * - phone owner
     *
     * =====================================================
     */
    private final ResolveUserByIdentifierService
        resolveUserByIdentifierService;


    /**
     * =====================================================
     * LOGIN FLOW
     * =====================================================
     */
    @Transactional(readOnly = true)
    public VerifyCredentialResponse execute(
        VerifyCredentialRequest request
    ) {

        // =================================================
        // 1. VALIDATE REQUEST
        // =================================================

        validateRequest(request);


        // =================================================
        // 2. NORMALIZE IDENTIFIER
        // =================================================
        //
        // Defensive normalization.
        //
        // Identity Service thường đã normalize.
        //
        // =================================================

        String normalizedIdentifier =
            identifierNormalizer.normalize(
                request.identifier(),
                request.identifierType()
            );


        // =================================================
        // 3. RESOLVE USER
        // =================================================

        User user =
            resolveUserByIdentifierService.resolve(
                normalizedIdentifier,
                request.identifierType()
            );

        if (user == null) {

            return failure(
                "IDENTITY_NOT_FOUND"
            );
        }


        // =================================================
        // 4. VERIFY PASSWORD
        // =================================================
        //
        // FUTURE:
        //
        // Sau này logic này nên chuyển sang:
        //
        // - credential aggregate
        // - credential domain service
        //
        // =================================================

        boolean passwordMatched =
            passwordHasher.matches(
                request.credential(),
                user.getPasswordHash()
            );

        if (!passwordMatched) {

            return failure(
                "INVALID_CREDENTIAL"
            );
        }


        // =================================================
        // 5. CHECK ACCOUNT STATE
        // =================================================
        //
        // FUTURE:
        //
        // Sau này:
        //
        // - account policy
        // - MFA policy
        // - anomaly detection
        // - device verification
        //
        // nên chuyển sang domain service riêng.
        //
        // =================================================

        if (user.getStatus()
            == UserStatus.DELETED) {

            return new VerifyCredentialResponse(
                false,
                null,
                "ACCOUNT_DELETED",
                true,
                false,
                false
            );
        }

        if (user.getStatus()
            == UserStatus.LOCKED) {

            return new VerifyCredentialResponse(
                false,
                null,
                "ACCOUNT_LOCKED",
                false,
                true,
                false
            );
        }

        // =================================================
        // 6. BUILD VERIFIED IDENTITY
        // =================================================

        VerifiedIdentity identity =
            new VerifiedIdentity(
                user.getId(),
                user.getUsername(),
                "HUMAN",
                request.shardId(),
                "user-service"
            );


        // =================================================
        // 7. SUCCESS RESPONSE
        // =================================================

        return new VerifyCredentialResponse(
            true,
            identity,
            null,
            false,
            false,
            false
        );
    }


    /**
     * =====================================================
     * FAILURE RESPONSE
     * =====================================================
     */
    private VerifyCredentialResponse failure(
        String reason
    ) {

        return new VerifyCredentialResponse(
            false,
            null,
            reason,
            false,
            false,
            false
        );
    }


    /**
     * =====================================================
     * VALIDATE REQUEST
     * =====================================================
     */
    private void validateRequest(
        VerifyCredentialRequest request
    ) {

        if (request == null) {

            throw new IllegalArgumentException(
                "request is null"
            );
        }

        if (request.identifier() == null
            || request.identifier().isBlank()) {

            throw new IllegalArgumentException(
                "identifier is invalid"
            );
        }

        if (request.identifierType() == null) {

            throw new IllegalArgumentException(
                "identifierType is null"
            );
        }

        if (request.credential() == null
            || request.credential().isBlank()) {

            throw new IllegalArgumentException(
                "credential is invalid"
            );
        }

        if (request.shardId() == null
            || request.shardId().isBlank()) {

            throw new IllegalArgumentException(
                "shardId is invalid"
            );
        }
    }
}