package vn.xime.user.application.usecase.contact;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.contact.model.UserContact;

import vn.xime.user.application.dto.external.contact.ContactResponse;
import vn.xime.user.application.mapper.contact.UserContactMapper;
import vn.xime.user.application.port.out.contact.UserContactRepository;


@Component
@RequiredArgsConstructor
public class SetPrimaryContactUseCase {

    private final UserContactRepository userContactRepository;

    private final UserContactMapper mapper;


    @Transactional
    public ContactResponse execute(
        String identifier,
        String contactId
    ) {

        /*
         * =========================
         * USER ID
         * =========================
         */

        Id userId = IdService.fromString(
            identifier
        );


        /*
         * =========================
         * LOAD TARGET CONTACT
         * =========================
         */

        Id id = IdService.fromString(
            contactId
        );

        UserContact target =
            userContactRepository.findById(id)
                .orElseThrow(
                    () -> new IllegalArgumentException(
                        "contact not found"
                    )
                );


        /*
         * =========================
         * VERIFY OWNERSHIP
         * =========================
         */

        if (!target.getUserId().equals(userId)) {

            throw new IllegalArgumentException(
                "contact not found"
            );
        }


        /*
         * =========================
         * UNMARK EXISTING PRIMARY
         * =========================
         */

        userContactRepository
            .findPrimaryContact(userId, target.getType())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing ->
                userContactRepository.save(
                    existing.unmarkPrimary()
                )
            );


        /*
         * =========================
         * MARK TARGET AS PRIMARY
         * =========================
         */

        UserContact saved =
            userContactRepository.save(
                target.markPrimary()
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponse(
            saved
        );
    }
}
