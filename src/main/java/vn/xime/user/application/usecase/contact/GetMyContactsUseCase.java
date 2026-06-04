package vn.xime.user.application.usecase.contact;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.contact.model.UserContact;

import vn.xime.user.application.dto.external.contact.ContactResponse;
import vn.xime.user.application.mapper.contact.UserContactMapper;
import vn.xime.user.application.port.out.contact.UserContactRepository;


@Component
@RequiredArgsConstructor
public class GetMyContactsUseCase {

    private final UserContactRepository userContactRepository;

    private final UserContactMapper mapper;


    @Transactional(readOnly = true)
    public List<ContactResponse> execute(
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
         * LOAD CONTACTS
         * =========================
         */

        List<UserContact> contacts =
            userContactRepository.findByUserId(
                userId
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponseList(
            contacts
        );
    }
}
