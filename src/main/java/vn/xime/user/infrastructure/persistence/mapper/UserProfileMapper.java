package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.profile.model.Gender;
import vn.xime.user.domain.profile.model.UserProfile;
import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.infrastructure.persistence.entity.UserProfileEntity;

import java.util.Arrays;

public class UserProfileMapper {

    // =========================
    // Entity -> Domain
    // =========================

    public static UserProfile toDomain(UserProfileEntity e) {

        if (e == null) {
            throw new IllegalArgumentException("UserProfileEntity must not be null");
        }

        requireNonNull(e.getUserId(), "userId");
        requireNonNull(e.getUpdatedAt(), "updatedAt");

        return new UserProfile(
                toId(e.getUserId()),
                e.getFullName(),
                e.getDisplayName(),
                e.getDateOfBirth(),
                mapGender(e.getGender()),
                e.getAvatarUrl(),
                e.getBio(),
                e.getCountry(),
                e.getLanguage(),
                e.getTimezone(),
                e.getUpdatedAt()
        );
    }

    // =========================
    // Domain -> Entity
    // =========================

    public static UserProfileEntity toEntity(UserProfile d) {

        if (d == null) {
            throw new IllegalArgumentException("UserProfile must not be null");
        }

        UserProfileEntity e = new UserProfileEntity();

        e.setUserId(toBytes(d.getUserId()));
        e.setFullName(d.getFullName());
        e.setDisplayName(d.getDisplayName());
        e.setDateOfBirth(d.getDateOfBirth());
        e.setGender(mapGender(d.getGender()));
        e.setAvatarUrl(d.getAvatarUrl());
        e.setBio(d.getBio());
        e.setCountry(d.getCountry());
        e.setLanguage(d.getLanguage());
        e.setTimezone(d.getTimezone());
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

    private static Gender mapGender(String gender) {

        if (gender == null) {
            return null;
        }

        try {
            return Gender.valueOf(gender.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid gender: " + gender);
        }
    }

    private static String mapGender(Gender gender) {
        return gender == null ? null : gender.name();
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