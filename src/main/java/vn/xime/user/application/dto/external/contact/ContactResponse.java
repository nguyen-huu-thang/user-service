package vn.xime.user.application.dto.external.contact;

import java.time.Instant;

import vn.xime.user.domain.contact.model.ContactType;

public record ContactResponse(

    String id,

    ContactType type,

    String value,

    boolean isVerified,

    boolean isPrimary,

    Instant createdAt
) {
}
