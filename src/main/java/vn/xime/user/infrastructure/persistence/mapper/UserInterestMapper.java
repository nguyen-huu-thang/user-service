package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.interest.model.UserInterest;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.infrastructure.persistence.entity.UserInterestEntity;

import java.util.Arrays;
import java.util.List;

public class UserInterestMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static UserInterest toDomain(UserInterestEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserInterestEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getUserId(), "userId");
        requireNonNull(e.getInterestId(), "interestId");
        requireNonNull(e.getWeight(), "weight");

        return new UserInterest(
                toId(e.getId()),              // 🔥 thêm id
                toId(e.getUserId()),
                toId(e.getInterestId()),
                e.getWeight()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserInterestEntity toEntity(UserInterest d) {

        if (d == null) {
            throw new IllegalArgumentException("UserInterest must not be null");
        }

        UserInterestEntity e = new UserInterestEntity();

        e.setId(toBytes(d.getId()));          // 🔥 thêm id
        e.setUserId(toBytes(d.getUserId()));
        e.setInterestId(toBytes(d.getInterestId()));
        e.setWeight(d.getWeight());

        return e;
    }

    // =========================
    // LIST MAPPING
    // =========================

    public static List<UserInterest> toDomainList(List<UserInterestEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(UserInterestMapper::toDomain).toList();
    }

    public static List<UserInterestEntity> toEntityList(List<UserInterest> domains) {
        if (domains == null) return List.of();
        return domains.stream().map(UserInterestMapper::toEntity).toList();
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