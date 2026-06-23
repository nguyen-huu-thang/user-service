package vn.xime.user.application.usecase.identity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.authentication.service.IdentifierNormalizer;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.user.model.User;
import vn.xime.user.domain.user.policy.PasswordPolicy;
import vn.xime.user.domain.user.service.RegistrationDomainService;
import vn.xime.user.domain.user.service.RegistrationDomainService.RegistrationResult;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;

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
 * RESPONSIBILITY (orchestration thuần)
 * =========================================================
 *
 * - validate request shape
 * - check password policy (domain)
 * - normalize identifier (domain)
 * - check identifier availability
 * - hash password (port)
 * - dựng aggregate qua RegistrationDomainService (domain)
 * - persist data
 *
 * Các quyết định nghiệp vụ (username theo identifierType, tạo
 * contact, password policy) đã được tách sang domain:
 * RegistrationDomainService + PasswordPolicy.
 *
 * =========================================================
 * FUTURE
 * =========================================================
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

    private final UserRepository userRepository;

    private final UserContactRepository userContactRepository;

    private final IdentifierNormalizer identifierNormalizer;

    private final CheckIdentifierAvailabilityService
        checkIdentifierAvailabilityService;

    private final PasswordHasher passwordHasher;

    private final PasswordPolicy passwordPolicy;

    private final RegistrationDomainService registrationDomainService;


    @Transactional
    public RegisterUserResponse execute(
        RegisterUserRequest request
    ) {

        // =================================================
        // 1. VALIDATE REQUEST SHAPE
        // =================================================

        validateRequest(request);


        // =================================================
        // 2. PASSWORD POLICY (DOMAIN)
        // =================================================

        passwordPolicy.validate(request.credential());


        // =================================================
        // 3. NORMALIZE IDENTIFIER
        // =================================================

        String normalizedIdentifier =
            identifierNormalizer.normalize(
                request.identifier(),
                request.identifierType()
            );


        // =================================================
        // 4. CHECK IDENTIFIER AVAILABILITY
        // =================================================
        //
        // IMPORTANT: chỉ là friendly validation, KHÔNG phải
        // uniqueness guarantee. Bảo vệ thật đến từ database
        // unique constraint.

        boolean alreadyExists =
            checkIdentifierAvailabilityService.exists(
                normalizedIdentifier,
                request.identifierType()
            );

        if (alreadyExists) {

            throw new PublicError(
                ErrorCode.IDENTIFIER_ALREADY_EXISTS
            );
        }


        // =================================================
        // 5. HASH PASSWORD (PORT)
        // =================================================

        String passwordHash =
            passwordHasher.hash(
                request.credential()
            );


        // =================================================
        // 6. BUILD AGGREGATE (DOMAIN)
        // =================================================
        //
        // RegistrationDomainService quyết định username +
        // contact theo identifierType.

        RegistrationResult registration =
            registrationDomainService.register(
                request.identifierType(),
                normalizedIdentifier,
                passwordHash
            );

        User user = registration.user();


        // =================================================
        // 7. PERSIST
        // =================================================

        userRepository.save(user);

        if (registration.contact() != null) {

            userContactRepository.save(
                registration.contact()
            );
        }


        // =================================================
        // 8. RETURN RESPONSE
        // =================================================

        return new RegisterUserResponse(
            IdService.toString(user.getId()),   // identityId
            "U00000",                           // shardId
            user.getCreatedAt()                 // createdAt
        );
    }


    /**
     * =====================================================
     * VALIDATE REQUEST SHAPE
     * =====================================================
     *
     * Chỉ kiểm tra hình dạng request. Password policy do
     * PasswordPolicy (domain) đảm nhiệm.
     */
    private void validateRequest(
        RegisterUserRequest request
    ) {

        if (request == null) {
            throw new IllegalArgumentException("request is null");
        }

        if (request.identifier() == null
            || request.identifier().isBlank()) {
            throw new IllegalArgumentException("identifier is invalid");
        }

        if (request.identifierType() == null) {
            throw new IllegalArgumentException("identifierType is null");
        }
    }
}
