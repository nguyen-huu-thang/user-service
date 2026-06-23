package vn.xime.user.application.usecase.contact;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.error.ErrorCode;
import vn.xime.user.common.exception.PublicError;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.contact.service.UserContactDomainService;
import vn.xime.user.domain.contact.service.UserContactDomainService.PrimaryAssignment;

import vn.xime.user.application.dto.external.contact.ContactResponse;
import vn.xime.user.application.mapper.contact.UserContactMapper;
import vn.xime.user.application.port.out.contact.UserContactRepository;


@Component
@RequiredArgsConstructor
public class SetPrimaryContactUseCase {

    private final UserContactRepository userContactRepository;

    private final UserContactDomainService userContactDomainService;

    private final UserContactMapper mapper;


    @Transactional
    public ContactResponse execute(
        String identifier,
        String contactId
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
         * LOAD TARGET CONTACT
         * =========================
         */

        Id id = IdService.fromString(
            contactId
        );

        UserContact target =
            userContactRepository.findById(id)
                .orElseThrow(
                    () -> new PublicError(
                        ErrorCode.CONTACT_NOT_FOUND
                    )
                );


        /*
         * =========================
         * VERIFY OWNERSHIP
         * =========================
         */

        if (!target.getUserId().equals(userId)) {

            throw new PublicError(
                ErrorCode.CONTACT_NOT_FOUND
            );
        }


        /*
         * =========================
         * APPLY PRIMARY RULE (DOMAIN)
         * =========================
         *
         * Quy tắc "mỗi type chỉ 1 primary, promote cái mới thì
         * demote cái cũ" là cross-entity rule -> đặt ở domain
         * service. Use case chỉ lo load primary hiện tại và lưu
         * kết quả.
         */

        PrimaryAssignment assignment =
            userContactDomainService.setPrimary(
                target,
                userContactRepository.findPrimaryContact(
                    userId,
                    target.getType()
                )
            );

        assignment.getDemoted().ifPresent(
            userContactRepository::save
        );

        UserContact saved =
            userContactRepository.save(
                assignment.getPromoted()
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
