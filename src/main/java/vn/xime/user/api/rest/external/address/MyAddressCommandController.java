package vn.xime.user.api.rest.external.address;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import vn.xime.user.application.dto.external.address.AddAddressRequest;
import vn.xime.user.application.dto.external.address.AddressResponse;
import vn.xime.user.application.dto.external.address.UpdateAddressRequest;

import vn.xime.user.application.usecase.address.AddMyAddressUseCase;
import vn.xime.user.application.usecase.address.DeleteMyAddressUseCase;
import vn.xime.user.application.usecase.address.UpdateMyAddressUseCase;


@RestController
@RequestMapping("/api/v1/me/addresses")
@RequiredArgsConstructor
public class MyAddressCommandController {

    private final AddMyAddressUseCase addMyAddressUseCase;

    private final UpdateMyAddressUseCase updateMyAddressUseCase;

    private final DeleteMyAddressUseCase deleteMyAddressUseCase;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse addAddress(

        Authentication authentication,

        @Valid
        @RequestBody
        AddAddressRequest request
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

        return addMyAddressUseCase.execute(
            identityId,
            request
        );
    }


    @PatchMapping("/{id}")
    public AddressResponse updateAddress(

        Authentication authentication,

        @PathVariable
        String id,

        @Valid
        @RequestBody
        UpdateAddressRequest request
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

        return updateMyAddressUseCase.execute(
            identityId,
            id,
            request
        );
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAddress(

        Authentication authentication,

        @PathVariable
        String id
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

        deleteMyAddressUseCase.execute(
            identityId,
            id
        );
    }
}
