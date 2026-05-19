package vn.xime.user.domain.user.factory;

import vn.xime.user.domain.address.model.AddressType;
import vn.xime.user.domain.address.model.UserAddress;
import vn.xime.user.domain.sharedkernel.factory.IdFactory;
import vn.xime.user.domain.sharedkernel.model.Id;

import java.time.Instant;

public class UserAddressFactory {

    public UserAddress create(
            Id userId,
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
        // =========================
        // VALIDATE (DOMAIN LEVEL)
        // =========================

        if (userId == null) {
            throw new IllegalArgumentException("userId is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("type is required");
        }

        if (addressLine != null && addressLine.isBlank()) {
            throw new IllegalArgumentException("addressLine must not be blank");
        }

        if (lat != null && (lat < -90 || lat > 90)) {
            throw new IllegalArgumentException("lat out of range");
        }

        if (lng != null && (lng < -180 || lng > 180)) {
            throw new IllegalArgumentException("lng out of range");
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be after startDate");
        }

        // =========================
        // BUILD DOMAIN
        // =========================

        Id id = IdFactory.generate();

        return new UserAddress(
                id,
                userId,
                type,
                country,
                city,
                region,
                addressLine,
                lat,
                lng,
                startDate,
                endDate
        );
    }
}