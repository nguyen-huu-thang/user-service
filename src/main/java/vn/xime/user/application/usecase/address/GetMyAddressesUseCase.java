package vn.xime.user.application.usecase.address;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;
import vn.xime.user.domain.address.model.UserAddress;

import vn.xime.user.application.dto.external.address.AddressResponse;
import vn.xime.user.application.mapper.address.UserAddressMapper;
import vn.xime.user.application.port.out.address.UserAddressRepository;


@Component
@RequiredArgsConstructor
public class GetMyAddressesUseCase {

    private final UserAddressRepository userAddressRepository;

    private final UserAddressMapper mapper;


    @Transactional(readOnly = true)
    public List<AddressResponse> execute(
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
         * LOAD ADDRESSES
         * =========================
         */

        List<UserAddress> addresses =
            userAddressRepository.findByUserId(
                userId
            );


        /*
         * =========================
         * RESPONSE
         * =========================
         */

        return mapper.toResponseList(
            addresses
        );
    }
}
