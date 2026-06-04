package vn.xime.user.application.usecase.address;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.address.model.UserAddress;

import vn.xime.user.application.dto.external.address.AddressResponse;
import vn.xime.user.application.dto.external.address.UpdateAddressRequest;
import vn.xime.user.application.mapper.address.UserAddressMapper;
import vn.xime.user.application.port.out.address.UserAddressRepository;


@Component
@RequiredArgsConstructor
public class UpdateMyAddressUseCase {

    private final UserAddressRepository userAddressRepository;

    private final UserAddressMapper mapper;


    @Transactional
    public AddressResponse execute(
        String identifier,
        String addressId,
        UpdateAddressRequest request
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
                    () -> new IllegalArgumentException(
                        "address not found"
                    )
                );


        /*
         * =========================
         * VERIFY OWNERSHIP
         * =========================
         */

        if (!address.getUserId().equals(userId)) {

            throw new IllegalArgumentException(
                "address not found"
            );
        }


        /*
         * =========================
         * UPDATE
         * =========================
         */

        UserAddress updated =
            address
                .changeType(request.type())
                .updateAddress(
                    request.country(),
                    request.city(),
                    request.region(),
                    request.addressLine(),
                    request.lat(),
                    request.lng(),
                    request.startDate(),
                    request.endDate()
                );


        /*
         * =========================
         * SAVE
         * =========================
         */

        UserAddress saved =
            userAddressRepository.save(
                updated
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
