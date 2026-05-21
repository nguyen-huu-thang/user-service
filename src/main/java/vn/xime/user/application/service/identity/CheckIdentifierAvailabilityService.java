package vn.xime.user.application.service.identity;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import vn.xime.user.application.port.out.contact.UserContactRepository;
import vn.xime.user.application.port.out.user.UserRepository;

import vn.xime.user.domain.authentication.model.IdentifierType;

import vn.xime.user.domain.contact.model.ContactType;

/**
 * =========================================================
 * CHECK IDENTIFIER AVAILABILITY SERVICE
 * =========================================================
 *
 * Check:
 *
 * - username availability
 * - email availability
 * - phone availability
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * Identifier MUST already be normalized.
 *
 * =========================================================
 */
@Service
@RequiredArgsConstructor
public class CheckIdentifierAvailabilityService {

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
     * CHECK EXISTS
     * =====================================================
     */
    public boolean exists(
        String identifier,
        IdentifierType identifierType
    ) {

        if (identifier == null
            || identifier.isBlank()) {

            throw new IllegalArgumentException(
                "identifier is invalid"
            );
        }

        if (identifierType == null) {

            throw new IllegalArgumentException(
                "identifierType is null"
            );
        }

        return switch (identifierType) {

            case USERNAME ->

                userRepository.existsByUsername(
                    identifier
                );

            case EMAIL ->

                userContactRepository
                    .existsByTypeAndValue(
                        ContactType.EMAIL,
                        identifier
                    );

            case PHONE ->

                userContactRepository
                    .existsByTypeAndValue(
                        ContactType.PHONE,
                        identifier
                    );
        };
    }
}