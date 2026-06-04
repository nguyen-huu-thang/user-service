package vn.xime.user.application.dto.external.contact;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import vn.xime.user.domain.contact.model.ContactType;

public record AddContactRequest(

    @NotNull
    ContactType type,

    @NotBlank
    String value
) {
}
