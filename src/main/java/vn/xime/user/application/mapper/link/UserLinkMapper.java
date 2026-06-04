package vn.xime.user.application.mapper.link;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.contact.model.UserLink;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.dto.external.link.LinkResponse;

@Component
public class UserLinkMapper {

    public LinkResponse toResponse(
        UserLink link
    ) {

        return new LinkResponse(

            IdService.toString(link.getId()),

            link.getType(),

            link.getUrl()
        );
    }

    public List<LinkResponse> toResponseList(
        List<UserLink> links
    ) {

        return links.stream()
            .map(this::toResponse)
            .toList();
    }
}
