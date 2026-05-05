package vn.xime.user.domain.model;

import java.time.Instant;
import java.util.Objects;

public class UserAddress {

    private final Id id;
    private final Id userId;

    private final AddressType type;

    private final String country;
    private final String city;
    private final String region;
    private final String addressLine;

    private final Double lat;
    private final Double lng;

    private final Instant startDate;
    private final Instant endDate;

    public UserAddress(
            Id id,
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
        this.id = Objects.requireNonNull(id);
        this.userId = Objects.requireNonNull(userId);
        this.type = Objects.requireNonNull(type);

        this.country = country;
        this.city = city;
        this.region = region;
        this.addressLine = addressLine;

        this.lat = lat;
        this.lng = lng;

        this.startDate = startDate;
        this.endDate = endDate;

        validate();
    }

    // =========================
    // VALIDATION
    // =========================

    private void validate() {
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
    }

    // =========================
    // BUSINESS LOGIC
    // =========================

    public boolean isCurrent(Instant now) {
        if (type == AddressType.CURRENT) {
            return true;
        }

        if (startDate != null && now.isBefore(startDate)) {
            return false;
        }

        if (endDate != null && !now.isBefore(endDate)) {
            return false;
        }

        return true;
    }

    public boolean isPast(Instant now) {
        if (endDate == null) {
            return false;
        }
        return !now.isBefore(endDate);
    }

    public boolean hasGeo() {
        return lat != null && lng != null;
    }

    // =========================
    // STATE CHANGE
    // =========================

    public UserAddress updateAddress(
            String country,
            String city,
            String region,
            String addressLine,
            Double lat,
            Double lng,
            Instant startDate,
            Instant endDate
    ) {
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

    public UserAddress changeType(AddressType newType) {
        return new UserAddress(
                id,
                userId,
                newType,
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

    // =========================
    // GETTERS
    // =========================

    public Id getId() {
        return id;
    }

    public Id getUserId() {
        return userId;
    }

    public AddressType getType() {
        return type;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getRegion() {
        return region;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }
}