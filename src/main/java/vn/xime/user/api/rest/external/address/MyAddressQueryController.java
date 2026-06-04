package vn.xime.user.api.rest.external.address;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.address.AddressResponse;

import vn.xime.user.application.usecase.address.GetMyAddressesUseCase;


@RestController
@RequestMapping("/api/v1/me/addresses")
@RequiredArgsConstructor
public class MyAddressQueryController {

    private final GetMyAddressesUseCase getMyAddressesUseCase;


    @GetMapping
    public List<AddressResponse> getMyAddresses(
        Authentication authentication
    ) {

        /*
         * =========================
         * AUTHENTICATED IDENTITY
         * =========================
         */

        String identityId = authentication.getName();


        /*
         * =========================
         * EXECUTE
         * =========================
         */

        return getMyAddressesUseCase.execute(
            identityId
        );
    }
}
