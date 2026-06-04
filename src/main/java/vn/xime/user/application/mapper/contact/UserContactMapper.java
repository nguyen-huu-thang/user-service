package vn.xime.user.application.mapper.contact;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.dto.external.contact.ContactResponse;

@Component
public class UserContactMapper {

    public ContactResponse toResponse(
        UserContact contact
    ) {

        return new ContactResponse(

            IdService.toString(contact.getId()),

            contact.getType(),

            contact.getValue(),

            contact.isVerified(),

            contact.isPrimary(),

            contact.getCreatedAt()
        );
    }

    public List<ContactResponse> toResponseList(
        List<UserContact> contacts
    ) {

        return contacts.stream()
            .map(this::toResponse)
            .toList();
    }
}
