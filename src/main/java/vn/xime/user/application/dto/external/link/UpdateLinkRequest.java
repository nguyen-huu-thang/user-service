package vn.xime.user.application.dto.external.link;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import vn.xime.user.domain.contact.model.LinkType;

public record UpdateLinkRequest(

    @NotNull
    LinkType type,

    @NotBlank
    String url
) {
}
