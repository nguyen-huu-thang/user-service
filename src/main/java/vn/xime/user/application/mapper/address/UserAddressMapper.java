package vn.xime.user.application.mapper.address;

import java.util.List;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.address.model.UserAddress;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.dto.external.address.AddressResponse;

@Component
public class UserAddressMapper {

    public AddressResponse toResponse(
        UserAddress address
    ) {

        return new AddressResponse(

            IdService.toString(address.getId()),

            address.getType(),

            address.getCountry(),

            address.getCity(),

            address.getRegion(),

            address.getAddressLine(),

            address.getLat(),

            address.getLng(),

            address.getStartDate(),

            address.getEndDate()
        );
    }

    public List<AddressResponse> toResponseList(
        List<UserAddress> addresses
    ) {

        return addresses.stream()
            .map(this::toResponse)
            .toList();
    }
}
