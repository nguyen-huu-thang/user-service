package vn.xime.user.application.usecase.link;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.contact.model.UserLink;

import vn.xime.user.application.dto.external.link.LinkResponse;
import vn.xime.user.application.mapper.link.UserLinkMapper;
import vn.xime.user.application.port.out.address.UserLinkRepository;


@Component
@RequiredArgsConstructor
public class GetMyLinksUseCase {

    private final UserLinkRepository userLinkRepository;

    private final UserLinkMapper mapper;


    @Transactional(readOnly = true)
    public List<LinkResponse> execute(
        String identifier
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
         * LOAD LINKS
         * =========================
         */

        List<UserLink> links =
            userLinkRepository.findByUserId(
                userId
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponseList(
            links
        );
    }
}
