package vn.xime.user.application.usecase.identity;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.authentication.service.IdentifierNormalizer;
import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.user.model.User;
import vn.xime.user.domain.user.model.UserStatus;

import vn.xime.user.application.service.identity.CheckIdentifierAvailabilityService;

import vn.xime.user.application.dto.external.identity.RegisterUserRequest;
import vn.xime.user.application.dto.external.identity.RegisterUserResponse;

import vn.xime.user.application.port.out.contact.UserContactRepository;
import vn.xime.user.application.port.out.crypto.PasswordHasher;
import vn.xime.user.application.port.out.user.UserRepository;


/**
 * =========================================================
 * REGISTER USER USE CASE
 * =========================================================
 *
 * Internal identity registration flow.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * - validate request
 * - normalize identifier
 * - check identifier availability
 * - hash password
 * - create user
 * - create contact
 * - persist data
 *
 * =========================================================
 * CURRENT STAGE
 * =========================================================
 *
 * Hiện tại:
 *
 * - passwordHash vẫn nằm trong User
 * - chưa có domain credential
 * - chỉ hỗ trợ password registration
 *
 * =========================================================
 * FUTURE
 * =========================================================
 *
 * Sau này:
 *
 * - password sẽ chuyển sang credential aggregate
 * - hỗ trợ OAuth / MFA / Passkey
 * - event-driven registration flow
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class RegisterUseCase {

    /**
     * =====================================================
     * USER REPOSITORY
     * =====================================================
     */
    private final UserRepository userRepository;


    /**
     * =====================================================
     * CONTACT REPOSITORY
     * =====================================================
     */
    private final UserContactRepository userContactRepository;


    /**
     * =====================================================
     * IDENTIFIER NORMALIZER
     * =====================================================
     *
     * Normalize:
     *
     * - username
     * - email
     * - phone
     *
     * NOTE:
     *
     * Identity Service thường đã normalize trước.
     *
     * Tuy nhiên User Service vẫn nên normalize
     * lại để defensive against:
     *
     * - cross-service inconsistency
     * - bug
     * - malformed request
     *
     * =====================================================
     */
    private final IdentifierNormalizer identifierNormalizer;


    /**
     * =====================================================
     * IDENTIFIER AVAILABILITY
     * =====================================================
     *
     * Check:
     *
     * - username already exists
     * - email already exists
     * - phone already exists
     *
     * =====================================================
     */
    private final CheckIdentifierAvailabilityService
        checkIdentifierAvailabilityService;


    /**
     * =====================================================
     * PASSWORD HASHER
     * =====================================================
     *
     * One-way password hashing.
     *
     * Example:
     *
     * - BCrypt
     * - Argon2id
     *
     * =====================================================
     */
    private final PasswordHasher passwordHasher;


    /**
     * =====================================================
     * REGISTER FLOW
     * =====================================================
     */
    @Transactional
    public RegisterUserResponse execute(
        RegisterUserRequest request
    ) {

        // =================================================
        // 1. VALIDATE REQUEST
        // =================================================

        validateRequest(request);


        // =================================================
        // 2. NORMALIZE IDENTIFIER
        // =================================================

        String normalizedIdentifier =
            identifierNormalizer.normalize(
                request.identifier(),
                request.identifierType()
            );


        // =================================================
        // 3. CHECK IDENTIFIER AVAILABILITY
        // =================================================
        //
        // IMPORTANT:
        //
        // Availability check chỉ là:
        //
        // - friendly validation
        //
        // KHÔNG phải uniqueness guarantee.
        //
        // Actual protection phải đến từ:
        //
        // - database unique constraint
        //
        // =================================================

        boolean alreadyExists =
            checkIdentifierAvailabilityService.exists(
                normalizedIdentifier,
                request.identifierType()
            );

        if (alreadyExists) {

            throw new IllegalArgumentException(
                "identifier already exists"
            );
        }


        // =================================================
        // 4. HASH PASSWORD
        // =================================================
        //
        // FUTURE:
        //
        // Sau này logic này nên chuyển sang:
        //
        // - credential domain service
        // - credential aggregate factory
        //
        // vì:
        //
        // - hash policy
        // - algorithm migration
        // - credential metadata
        //
        // đều là credential domain concern.
        //
        // =================================================

        String passwordHash =
            passwordHasher.hash(
                request.credential()
            );


        // =================================================
        // 5. CREATE USER ID
        // =================================================
        //
        // FUTURE:
        //
        // Sau này có thể chuyển:
        //
        // - user creation flow
        // - shard-aware allocation
        // - registration orchestration
        //
        // sang domain/application service riêng.
        //
        // =================================================

        Id userId = IdFactory.generate();


        // =================================================
        // 6. RESOLVE USERNAME
        // =================================================


        String username;

        switch (request.identifierType()) {

            case USERNAME -> {

                username = normalizedIdentifier;
            }

            case EMAIL, PHONE -> {

                username = null;
            }

            default -> {

                throw new IllegalStateException(
                    "unsupported identifier type"
                );
            }
        }


        // =================================================
        // 7. CREATE USER DOMAIN MODEL
        // =================================================
        //
        // CURRENT:
        //
        // User vẫn chứa:
        //
        // - passwordHash
        //
        // FUTURE:
        //
        // passwordHash sẽ được tách khỏi User.
        //
        // User chỉ giữ:
        //
        // - account state
        // - profile metadata
        //
        // Credential sẽ nằm ở:
        //
        // - user_credentials
        // - credential aggregate
        //
        // =================================================

        Instant now = Instant.now();

        User user =
            new User(
                userId,
                username,
                passwordHash,
                UserStatus.ACTIVE,
                now,
                null
            );


        // =================================================
        // 8. SAVE USER
        // =================================================
        //
        // FUTURE:
        //
        // Sau này:
        //
        // - catch unique constraint violation
        // - map thành domain exception
        //
        // Ví dụ:
        //
        // - UsernameAlreadyExistsException
        // - EmailAlreadyExistsException
        //
        // =================================================

        userRepository.save(user);


        // =================================================
        // 9. CREATE CONTACT
        // =================================================
        //
        // CURRENT:
        //
        // Contact chỉ được tạo cho:
        //
        // - EMAIL
        // - PHONE
        //
        // FUTURE:
        //
        // Logic này nên chuyển sang:
        //
        // - UserContactFactory
        // - ContactDomainService
        //
        // để:
        //
        // - verification policy
        // - primary contact policy
        // - metadata handling
        //
        // được centralized.
        //
        // =================================================

        UserContact userContact = null;

        switch (request.identifierType()) {

            case EMAIL -> {

                userContact =
                    new UserContact(
                        IdFactory.generate(),
                        userId,
                        ContactType.EMAIL,
                        normalizedIdentifier,
                        false,
                        true,
                        now
                    );
            }

            case PHONE -> {

                userContact =
                    new UserContact(
                        IdFactory.generate(),
                        userId,
                        ContactType.PHONE,
                        normalizedIdentifier,
                        false,
                        true,
                        now
                    );
            }

            case USERNAME -> {

                // =====================================
                // NO CONTACT CREATED
                // =====================================
            }

            default -> {

                throw new IllegalStateException(
                    "unsupported identifier type"
                );
            }
        }


        // =================================================
        // 10. SAVE CONTACT
        // =================================================
        //
        // FUTURE:
        //
        // Sau này có thể:
        //
        // - publish verification event
        // - send verification email
        // - update search index
        // - outbox pattern
        //
        // =================================================

        if (userContact != null) {

            userContactRepository.save(
                userContact
            );
        }


        // =================================================
        // 11. RETURN RESPONSE
        // =================================================

        return new RegisterUserResponse(
            IdService.toString(user.getId()),   // identityId
            "U00000",   // shardId
            user.getCreatedAt() // createdAt
        );
    }


    /**
     * =====================================================
     * VALIDATE REQUEST
     * =====================================================
     */
    private void validateRequest(
        RegisterUserRequest request
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


        // =================================================
        // SIMPLE PASSWORD POLICY
        // =================================================
        //
        // FUTURE:
        //
        // Password policy nên chuyển sang:
        //
        // - PasswordPolicyService
        // - CredentialPolicyService
        //
        // =================================================

        if (request.credential().length() < 6) {

            throw new IllegalArgumentException(
                "password too short"
            );
        }
    }
}