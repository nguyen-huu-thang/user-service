package vn.xime.user.domain.address.service;

import java.time.Instant;
import java.util.List;

import vn.xime.user.domain.address.model.AddressType;
import vn.xime.user.domain.address.model.UserAddress;

public class UserAddressValidationService {

    // =========================
    // BASIC VALIDATION
    // =========================

    public void validateNewAddress(
        AddressType type,
        String addressLine,
        Double lat,
        Double lng,
        Instant startDate,
        Instant endDate
    ) {
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
    }

    // =========================
    // CURRENT ADDRESS RULE
    // =========================

    /**
     * đảm bảo chỉ có 1 CURRENT address
     */
    public void ensureSingleCurrentAddress(
            List<UserAddress> addresses
    ) {
        long count = addresses.stream()
                .filter(a -> a.getType() == AddressType.CURRENT)
                .count();

        if (count > 1) {
            throw new IllegalStateException("Multiple CURRENT addresses detected");
        }
    }

    // =========================
    // TIME OVERLAP (IMPORTANT)
    // =========================

    /**
     * kiểm tra address mới có overlap với address cũ không
     */
    public void validateNoTimeOverlap(
            List<UserAddress> existing,
            Instant newStart,
            Instant newEnd
    ) {
        for (UserAddress a : existing) {

            Instant start = a.getStartDate();
            Instant end = a.getEndDate();

            if (start == null || end == null || newStart == null || newEnd == null) {
                continue; // bỏ qua open interval (MVP)
            }

            boolean overlap =
                    !newEnd.isBefore(start) && !newStart.isAfter(end);

            if (overlap) {
                throw new IllegalStateException("Address time range overlaps");
            }
        }
    }

    // =========================
    // BUSINESS CHECK
    // =========================

    public void ensureAddressExists(UserAddress address) {
        if (address == null) {
            throw new IllegalArgumentException("UserAddress is required");
        }
    }

    public void validateTypeChange(
            UserAddress address,
            AddressType newType
    ) {
        ensureAddressExists(address);

        if (newType == null) {
            throw new IllegalArgumentException("newType is required");
        }
    }

    // =========================
    // GEO VALIDATION (OPTIONAL)
    // =========================

    public void validateGeo(Double lat, Double lng) {
        if (lat == null && lng == null) {
            return;
        }

        if (lat == null || lng == null) {
            throw new IllegalArgumentException("lat/lng must be both present");
        }
    }
}