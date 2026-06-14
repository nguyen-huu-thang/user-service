package vn.xime.user.application.usecase.address;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.sharedkernel.error.ErrorCode;
import vn.xime.user.domain.sharedkernel.error.PublicError;
import vn.xime.user.domain.address.model.UserAddress;

import vn.xime.user.application.port.out.address.UserAddressRepository;


@Component
@RequiredArgsConstructor
public class DeleteMyAddressUseCase {

    private final UserAddressRepository userAddressRepository;


    @Transactional
    public void execute(
        String identifier,
        String addressId
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
         * LOAD ADDRESS
         * =========================
         */

        Id id = IdService.fromString(
            addressId
        );

        UserAddress address =
            userAddressRepository.findById(id)
                .orElseThrow(
                    () -> new PublicError(
                        ErrorCode.ADDRESS_NOT_FOUND
                    )
                );


        /*
         * =========================
         * VERIFY OWNERSHIP
         * =========================
         */

        if (!address.getUserId().equals(userId)) {

            throw new PublicError(
                ErrorCode.ADDRESS_NOT_FOUND
            );
        }


        /*
         * =========================
         * DELETE
         * =========================
         */

        userAddressRepository.deleteById(id);
    }
}
