package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.user.model.UserUsernameHistory;
import vn.xime.user.infrastructure.persistence.entity.UserUsernameHistoryEntity;

import java.util.Arrays;
import java.util.List;

public class UserUsernameHistoryMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static UserUsernameHistory toDomain(UserUsernameHistoryEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserUsernameHistoryEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getUserId(), "userId");
        requireNonNull(e.getOldUsername(), "oldUsername");
        requireNonNull(e.getChangedAt(), "changedAt");

        return new UserUsernameHistory(
                toId(e.getId()),
                toId(e.getUserId()),
                e.getOldUsername(),
                e.getChangedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserUsernameHistoryEntity toEntity(UserUsernameHistory d) {

        if (d == null) {
            throw new IllegalArgumentException("UserUsernameHistory must not be null");
        }

        UserUsernameHistoryEntity e = new UserUsernameHistoryEntity();

        e.setId(toBytes(d.getId()));
        e.setUserId(toBytes(d.getUserId()));
        e.setOldUsername(d.getOldUsername());
        e.setChangedAt(d.getChangedAt());

        return e;
    }

    // =========================
    // LIST MAPPING
    // =========================

    public static List<UserUsernameHistory> toDomainList(List<UserUsernameHistoryEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(UserUsernameHistoryMapper::toDomain).toList();
    }

    public static List<UserUsernameHistoryEntity> toEntityList(List<UserUsernameHistory> domains) {
        if (domains == null) return List.of();
        return domains.stream().map(UserUsernameHistoryMapper::toEntity).toList();
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
    // HELPERS
    // =========================

    private static void requireNonNull(Object value, String field) {
        if (value == null) {
            throw new IllegalStateException(field + " must not be null");
        }
    }
}