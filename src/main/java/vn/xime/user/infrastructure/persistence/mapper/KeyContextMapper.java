package vn.xime.user.infrastructure.persistence.mapper;

import vn.xime.user.domain.authentication.model.KeyContext;
import vn.xime.user.infrastructure.persistence.entity.KeyContextEntity;

public final class KeyContextMapper {

    private KeyContextMapper() {

    }

    // =================================================
    // ENTITY -> DOMAIN
    // =================================================

    public static KeyContext toDomain(
            KeyContextEntity entity
    ) {

        if (entity == null) {
            throw new IllegalArgumentException(
                    "KeyContextEntity must not be null"
            );
        }

        requireNonNull(
                entity.getKeyId(),
                "keyId"
        );

        requireNonNull(
                entity.getKeyType(),
                "keyType"
        );

        requireNonNull(
                entity.getKeySpec(),
                "keySpec"
        );

        requireNonNull(
                entity.getVerifierId(),
                "verifierId"
        );

        requireNonNull(
                entity.getPublicKey(),
                "publicKey"
        );

        requireNonNull(
                entity.getActivateAt(),
                "activateAt"
        );

        requireNonNull(
                entity.getExpiresAt(),
                "expiresAt"
        );

        return new KeyContext(
                entity.getKeyId(),
                entity.getKeyType(),
                entity.getKeySpec(),
                entity.getVerifierId(),
                entity.getPublicKey(),
                entity.getActivateAt(),
                entity.getExpiresAt()
        );
    }

    // =================================================
    // DOMAIN -> ENTITY
    // =================================================

    public static KeyContextEntity toEntity(
            KeyContext domain
    ) {

        if (domain == null) {
            throw new IllegalArgumentException(
                    "KeyContext must not be null"
            );
        }

        KeyContextEntity entity =
                new KeyContextEntity();

        entity.setKeyId(
                domain.getKeyId()
        );

        entity.setKeyType(
                domain.getKeyType()
        );

        entity.setKeySpec(
                domain.getKeySpec()
        );

        entity.setVerifierId(
                domain.getVerifierId()
        );

        entity.setPublicKey(
                domain.getPublicKey()
        );

        entity.setActivateAt(
                domain.getActivateAt()
        );

        entity.setExpiresAt(
                domain.getExpiresAt()
        );

        return entity;
    }

    // =================================================
    // HELPERS
    // =================================================

    private static void requireNonNull(
            Object value,
            String field
    ) {

        if (value == null) {
            throw new IllegalStateException(
                    field + " must not be null"
            );
        }
    }
}