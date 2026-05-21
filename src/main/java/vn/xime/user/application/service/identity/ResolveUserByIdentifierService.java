package vn.xime.user.application.service.identity;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import vn.xime.user.domain.authentication.model.IdentifierType;
import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.User;

import vn.xime.user.application.port.out.contact.UserContactRepository;
import vn.xime.user.application.port.out.user.UserRepository;

import vn.xime.user.application.usecase.identity.LoginUseCase
    .ResolvedUser;


/**
 * =========================================================
 * RESOLVE USER BY IDENTIFIER SERVICE
 * =========================================================
 *
 * Resolve user owner từ identifier.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * Resolve:
 *
 * - username
 * - email
 * - phone
 *
 * thành:
 *
 * - User
 * - UserContact (optional)
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * USERNAME:
 *
 * - contact = null
 *
 * EMAIL / PHONE:
 *
 * - contact != null
 *
 * =========================================================
 * CURRENT STAGE
 * =========================================================
 *
 * Hiện tại:
 *
 * - lookup trực tiếp qua repository
 * - chưa có distributed search
 * - chưa có cache
 *
 * =========================================================
 * FUTURE
 * =========================================================
 *
 * Sau này có thể:
 *
 * - add cache
 * - add read model
 * - shard-aware optimization
 * - distributed lookup
 *
 * =========================================================
 */
@Service
@RequiredArgsConstructor
public class ResolveUserByIdentifierService {

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
    private final UserContactRepository
        userContactRepository;


    /**
     * =====================================================
     * RESOLVE USER
     * =====================================================
     */
    public ResolvedUser resolve(
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

                resolveByUsername(
                    identifier
                );

            case EMAIL ->

                resolveByContact(
                    ContactType.EMAIL,
                    identifier
                );

            case PHONE ->

                resolveByContact(
                    ContactType.PHONE,
                    identifier
                );
        };
    }


    /**
     * =====================================================
     * RESOLVE USERNAME OWNER
     * =====================================================
     */
    private ResolvedUser resolveByUsername(
        String username
    ) {

        Optional<User> optionalUser =
            userRepository.findByUsername(
                username
            );

        if (optionalUser.isEmpty()) {

            return null;
        }

        return new ResolvedUser(
            optionalUser.get(),
            null
        );
    }


    /**
     * =====================================================
     * RESOLVE CONTACT OWNER
     * =====================================================
     *
     * Resolve:
     *
     * - email owner
     * - phone owner
     *
     * =====================================================
     */
    private ResolvedUser resolveByContact(
        ContactType contactType,
        String value
    ) {

        Optional<UserContact> optionalContact =
            userContactRepository
                .findByTypeAndValue(
                    contactType,
                    value
                );

        if (optionalContact.isEmpty()) {

            return null;
        }

        UserContact contact =
            optionalContact.get();

        Id userId =
            contact.getUserId();

        Optional<User> optionalUser =
            userRepository.findById(
                userId
            );

        if (optionalUser.isEmpty()) {

            // =============================================
            // DATA INCONSISTENCY
            // =============================================
            //
            // Contact tồn tại nhưng user không tồn tại.
            //
            // Có thể:
            //
            // - deleted data
            // - corrupted relation
            // - partial migration
            //
            // =============================================

            return null;
        }

        return new ResolvedUser(
            optionalUser.get(),
            contact
        );
    }
}