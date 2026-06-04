package vn.xime.user.application.dto.external.link;

import vn.xime.user.domain.contact.model.LinkType;

public record LinkResponse(

    String id,

    LinkType type,

    String url
) {
}
