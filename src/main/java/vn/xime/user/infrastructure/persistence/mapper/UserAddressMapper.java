package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.model.AddressType;
import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.UserAddress;
import vn.xime.user.infrastructure.persistence.entity.UserAddressEntity;

import java.util.Arrays;

public class UserAddressMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static UserAddress toDomain(UserAddressEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserAddressEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getUserId(), "userId");
        requireNonNull(e.getType(), "type");

        return new UserAddress(
                toId(e.getId()),
                toId(e.getUserId()),
                mapType(e.getType()),
                e.getCountry(),
                e.getCity(),
                e.getRegion(),
                e.getAddressLine(),
                e.getLat(),
                e.getLng(),
                e.getStartDate(),
                e.getEndDate()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserAddressEntity toEntity(UserAddress d) {

        if (d == null) {
            throw new IllegalArgumentException("UserAddress must not be null");
        }

        UserAddressEntity e = new UserAddressEntity();

        e.setId(toBytes(d.getId()));
        e.setUserId(toBytes(d.getUserId()));
        e.setType(d.getType().name());
        e.setCountry(d.getCountry());
        e.setCity(d.getCity());
        e.setRegion(d.getRegion());
        e.setAddressLine(d.getAddressLine());
        e.setLat(d.getLat());
        e.setLng(d.getLng());
        e.setStartDate(d.getStartDate());
        e.setEndDate(d.getEndDate());

        return e;
    }

    // =========================
    // ID MAPPING
    // =========================

    private static Id toId(byte[] bytes) {
        return new Id(copy(bytes));
    }

    private static byte[] toBytes(Id id) {
        return copy(id.toBytes());
    }

    private static byte[] copy(byte[] src) {
        return src == null ? null : Arrays.copyOf(src, src.length);
    }

    // =========================
    // ENUM MAPPING
    // =========================

    private static AddressType mapType(String type) {

        try {
            return AddressType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid address type: " + type);
        }
    }

    // =========================
    // HELPERS
    // =========================

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}