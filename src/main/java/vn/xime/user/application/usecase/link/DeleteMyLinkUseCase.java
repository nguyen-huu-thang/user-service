package vn.xime.user.application.usecase.link;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.contact.model.UserLink;

import vn.xime.user.application.port.out.address.UserLinkRepository;


@Component
@RequiredArgsConstructor
public class DeleteMyLinkUseCase {

    private final UserLinkRepository userLinkRepository;


    @Transactional
    public void execute(
        String identifier,
        String linkId
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
         * LOAD LINK
         * =========================
         */

        Id id = IdService.fromString(
            linkId
        );

        UserLink link =
            userLinkRepository.findById(id)
                .orElseThrow(
                    () -> new PublicError(
                        ErrorCode.LINK_NOT_FOUND
                    )
                );


        /*
         * =========================
         * VERIFY OWNERSHIP
         * =========================
         */

        if (!link.getUserId().equals(userId)) {

            throw new PublicError(
                ErrorCode.LINK_NOT_FOUND
            );
        }


        /*
         * =========================
         * DELETE
         * =========================
         */

        userLinkRepository.deleteById(id);
    }
}
