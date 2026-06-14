package vn.xime.user.application.usecase.contact;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.sharedkernel.error.ErrorCode;
import vn.xime.user.domain.sharedkernel.error.PublicError;
import vn.xime.user.domain.contact.model.UserContact;

import vn.xime.user.application.port.out.contact.UserContactRepository;


@Component
@RequiredArgsConstructor
public class DeleteMyContactUseCase {

    private final UserContactRepository userContactRepository;


    @Transactional
    public void execute(
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
         * LOAD CONTACT
         * =========================
         */

        Id id = IdService.fromString(
            contactId
        );

        UserContact contact =
            userContactRepository.findById(id)
                .orElseThrow(
                    () -> new PublicError(
                        ErrorCode.CONTACT_NOT_FOUND
                    )
                );


        /*
         * =========================
         * VERIFY OWNERSHIP
         * =========================
         */

        if (!contact.getUserId().equals(userId)) {

            throw new PublicError(
                ErrorCode.CONTACT_NOT_FOUND
            );
        }


        /*
         * =========================
         * DELETE
         * =========================
         */

        userContactRepository.deleteById(id);
    }
}
