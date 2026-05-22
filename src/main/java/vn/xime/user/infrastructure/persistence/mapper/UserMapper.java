package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.User;
import vn.xime.user.domain.user.model.UserStatus;
import vn.xime.user.infrastructure.persistence.entity.UserEntity;

import java.util.Arrays;

public class UserMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static User toDomain(UserEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getUsername(), "username");
        requireNonNull(e.getPasswordHash(), "passwordHash");
        requireNonNull(e.getStatus(), "status");
        requireNonNull(e.getCreatedAt(), "createdAt");

        return new User(
                toId(e.getId()),
                e.getUsername(),
                e.getPasswordHash(),
                mapStatus(e.getStatus()),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserEntity toEntity(User d) {

        if (d == null) {
            throw new IllegalArgumentException("User must not be null");
        }

        UserEntity e = new UserEntity();

        e.setId(toBytes(d.getId()));
        e.setUsername(d.getUsername());
        e.setPasswordHash(d.getPasswordHash());
        e.setStatus(d.getStatus().name());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());

        return e;
    }

    // =========================
    // ID MAPPING (QUAN TRỌNG)
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

    private static UserStatus mapStatus(String status) {

        try {
            return UserStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid user status: " + status);
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