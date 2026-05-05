package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.model.Id;
import vn.xime.user.domain.model.LinkType;
import vn.xime.user.domain.model.UserLink;
import vn.xime.user.infrastructure.persistence.entity.UserLinkEntity;

import java.util.Arrays;
import java.util.List;

public class UserLinkMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static UserLink toDomain(UserLinkEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserLinkEntity must not be null");
        }

        requireNonNull(e.getId(), "id");
        requireNonNull(e.getUserId(), "userId");
        requireNonNull(e.getType(), "type");
        requireNonNull(e.getUrl(), "url");

        return new UserLink(
                toId(e.getId()),
                toId(e.getUserId()),
                mapType(e.getType()),
                e.getUrl()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserLinkEntity toEntity(UserLink d) {

        if (d == null) {
            throw new IllegalArgumentException("UserLink must not be null");
        }

        UserLinkEntity e = new UserLinkEntity();

        e.setId(toBytes(d.getId()));
        e.setUserId(toBytes(d.getUserId()));
        e.setType(d.getType().name());
        e.setUrl(d.getUrl());

        return e;
    }

    // =========================
    // LIST MAPPING
    // =========================

    public static List<UserLink> toDomainList(List<UserLinkEntity> entities) {
        if (entities == null) return List.of();
        return entities.stream().map(UserLinkMapper::toDomain).toList();
    }

    public static List<UserLinkEntity> toEntityList(List<UserLink> domains) {
        if (domains == null) return List.of();
        return domains.stream().map(UserLinkMapper::toEntity).toList();
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

    private static LinkType mapType(String type) {

        try {
            return LinkType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid link type: " + type);
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