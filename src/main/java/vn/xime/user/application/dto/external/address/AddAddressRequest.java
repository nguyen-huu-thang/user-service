package vn.xime.user.application.dto.external.address;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

import vn.xime.user.domain.address.model.AddressType;

public record AddAddressRequest(

    @NotNull
    AddressType type,

    String country,

    String city,

    String region,

    String addressLine,

    Double lat,

    Double lng,

    Instant startDate,

    Instant endDate
) {
}
