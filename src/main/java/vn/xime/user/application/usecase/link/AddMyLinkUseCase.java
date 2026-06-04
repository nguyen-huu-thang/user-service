package vn.xime.user.application.usecase.link;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.contact.model.UserLink;

import vn.xime.user.application.dto.external.link.AddLinkRequest;
import vn.xime.user.application.dto.external.link.LinkResponse;
import vn.xime.user.application.mapper.link.UserLinkMapper;
import vn.xime.user.application.port.out.address.UserLinkRepository;


@Component
@RequiredArgsConstructor
public class AddMyLinkUseCase {

    private final UserLinkRepository userLinkRepository;

    private final UserLinkMapper mapper;


    @Transactional
    public LinkResponse execute(
        String identifier,
        AddLinkRequest request
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
         * CREATE LINK
         * =========================
         */

        UserLink link =
            new UserLink(
                IdFactory.generate(),
                userId,
                request.type(),
                request.url()
            );


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserLink saved =
            userLinkRepository.save(
                link
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
