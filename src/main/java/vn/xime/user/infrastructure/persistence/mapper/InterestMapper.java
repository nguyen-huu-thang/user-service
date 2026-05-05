package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.Interest;
import vn.xime.user.infrastructure.persistence.entity.InterestEntity;

import java.util.Arrays;
import java.util.List;

public class InterestMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static Interest toDomain(InterestEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("InterestEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getName(), "name");

        return new Interest(
                toId(e.getId()),
                e.getName()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static InterestEntity toEntity(Interest d) {

        if (d == null) {
            throw new IllegalArgumentException("Interest must not be null");
        }

        InterestEntity e = new InterestEntity();

        e.setId(toBytes(d.getId()));
        e.setName(d.getName());

        return e;
    }

    // =========================
    // LIST MAPPING
    // =========================

    public static List<Interest> toDomainList(List<InterestEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(InterestMapper::toDomain).toList();
    }

    public static List<InterestEntity> toEntityList(List<Interest> domains) {
        if (domains == null) return List.of();
        return domains.stream().map(InterestMapper::toEntity).toList();
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