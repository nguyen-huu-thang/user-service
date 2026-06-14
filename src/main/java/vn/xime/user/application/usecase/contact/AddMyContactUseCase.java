package vn.xime.user.application.usecase.contact;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.contact.model.UserContact;

import vn.xime.user.application.dto.external.contact.AddContactRequest;
import vn.xime.user.application.dto.external.contact.ContactResponse;
import vn.xime.user.application.mapper.contact.UserContactMapper;
import vn.xime.user.application.port.out.contact.UserContactRepository;


@Component
@RequiredArgsConstructor
public class AddMyContactUseCase {

    private final UserContactRepository userContactRepository;

    private final UserContactMapper mapper;


    @Transactional
    public ContactResponse execute(
        String identifier,
        AddContactRequest request
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
         * CHECK UNIQUENESS
         * =========================
         */

        boolean alreadyExists =
            userContactRepository.existsByTypeAndValue(
                request.type(),
                request.value()
            );

        if (alreadyExists) {

            throw new PublicError(
                ErrorCode.CONTACT_ALREADY_EXISTS
            );
        }


        /*
         * =========================
         * CREATE CONTACT
         * =========================
         */

        UserContact contact =
            new UserContact(
                IdFactory.generate(),
                userId,
                request.type(),
                request.value(),
                false,
                false,
                Instant.now()
            );


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserContact saved =
            userContactRepository.save(
                contact
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
