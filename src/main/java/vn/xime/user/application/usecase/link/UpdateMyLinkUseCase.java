package vn.xime.user.application.usecase.link;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.contact.model.UserLink;

import vn.xime.user.application.dto.external.link.LinkResponse;
import vn.xime.user.application.dto.external.link.UpdateLinkRequest;
import vn.xime.user.application.mapper.link.UserLinkMapper;
import vn.xime.user.application.port.out.address.UserLinkRepository;


@Component
@RequiredArgsConstructor
public class UpdateMyLinkUseCase {

    private final UserLinkRepository userLinkRepository;

    private final UserLinkMapper mapper;


    @Transactional
    public LinkResponse execute(
        String identifier,
        String linkId,
        UpdateLinkRequest request
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
         * UPDATE
         * =========================
         */

        UserLink updated =
            link
                .changeType(request.type())
                .changeUrl(request.url());


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserLink saved =
            userLinkRepository.save(
                updated
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
