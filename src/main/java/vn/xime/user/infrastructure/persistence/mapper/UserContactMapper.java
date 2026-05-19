package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.contact.model.ContactType;
import vn.xime.user.domain.contact.model.UserContact;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.infrastructure.persistence.entity.UserContactEntity;

import java.util.Arrays;
import java.util.List;

public class UserContactMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static UserContact toDomain(UserContactEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserContactEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getUserId(), "userId");
        requireNonNull(e.getType(), "type");
        requireNonNull(e.getValue(), "value");
        requireNonNull(e.getIsVerified(), "isVerified");
        requireNonNull(e.getIsPrimary(), "isPrimary");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new UserContact(
                toId(e.getId()),
                toId(e.getUserId()),
                mapType(e.getType()),
                e.getValue(),
                e.getIsVerified(),
                e.getIsPrimary(),
                e.getCreatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserContactEntity toEntity(UserContact d) {

        if (d == null) {
            throw new IllegalArgumentException("UserContact must not be null");
        }

        UserContactEntity e = new UserContactEntity();

        e.setId(toBytes(d.getId()));
        e.setUserId(toBytes(d.getUserId()));
        e.setType(d.getType().name());
        e.setValue(d.getValue());
        e.setIsVerified(d.isVerified());
        e.setIsPrimary(d.isPrimary());
        e.setCreatedAt(d.getCreatedAt());

        return e;
    }

    // =========================
    // LIST MAPPING (QUAN TRỌNG)
    // =========================

    public static List<UserContact> toDomainList(List<UserContactEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(UserContactMapper::toDomain).toList();
    }

    public static List<UserContactEntity> toEntityList(List<UserContact> domains) {
        if (domains == null) return List.of();
        return domains.stream().map(UserContactMapper::toEntity).toList();
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

    private static ContactType mapType(String type) {

        try {
            return ContactType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid contact type: " + type);
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