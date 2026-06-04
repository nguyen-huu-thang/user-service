package vn.xime.user.application.usecase.address;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.address.model.UserAddress;

import vn.xime.user.application.dto.external.address.AddAddressRequest;
import vn.xime.user.application.dto.external.address.AddressResponse;
import vn.xime.user.application.mapper.address.UserAddressMapper;
import vn.xime.user.application.port.out.address.UserAddressRepository;


@Component
@RequiredArgsConstructor
public class AddMyAddressUseCase {

    private final UserAddressRepository userAddressRepository;

    private final UserAddressMapper mapper;


    @Transactional
    public AddressResponse execute(
        String identifier,
        AddAddressRequest request
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
         * CREATE ADDRESS
         * =========================
         */

        UserAddress address =
            new UserAddress(
                IdFactory.generate(),
                userId,
                request.type(),
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
                address
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
