package vn.xime.user.domain.authentication.service;

import java.text.Normalizer;
import java.util.Locale;

import vn.xime.user.domain.authentication.model.IdentifierType;

/**
 * =========================================================
 * IDENTIFIER NORMALIZER
 * =========================================================
 *
 * Domain service dùng để normalize identifier
 * phục vụ:
 *
 * - deterministic routing
 * - credential lookup
 * - distributed search
 * - duplicate prevention
 *
 * =========================================================
 * IMPORTANT
 * =========================================================
 *
 * Normalization KHÔNG phải:
 *
 * - fuzzy correction
 * - typo fixing
 * - auto-recovery
 *
 * Input không hợp lệ nên bị reject
 * ở validation layer.
 *
 * =========================================================
 * RULES
 * =========================================================
 *
 * EMAIL:
 * - trim
 * - lowercase
 * - unicode normalize
 *
 * PHONE:
 * - trim
 * - remove spaces
 * - remove formatting chars
 *
 * USERNAME:
 * - trim
 * - lowercase
 * - unicode normalize
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * Hàm normalize phải deterministic:
 *
 * normalize(normalize(x))
 * ==
 * normalize(x)
 *
 * =========================================================
 */
public class IdentifierNormalizer {

    /**
     * =====================================================
     * NORMALIZE IDENTIFIER
     * =====================================================
     */
    public String normalize(
        String rawIdentifier,
        IdentifierType identifierType
    ) {

        if (rawIdentifier == null) {
            throw new IllegalArgumentException(
                "rawIdentifier is null"
            );
        }

        if (identifierType == null) {

            throw new IllegalArgumentException(
                "identifierType is invalid"
            );
        }

        return switch (identifierType) {

            case USERNAME ->
                normalizeUsername(rawIdentifier);

            case PHONE ->
                normalizePhone(rawIdentifier);

            case EMAIL ->
                normalizeEmail(rawIdentifier);

            default ->
                throw new IllegalArgumentException(
                    "unsupported identifierType"
                );
        };
    }


    /**
     * =====================================================
     * EMAIL NORMALIZATION
     * =====================================================
     */
    private String normalizeEmail(
            String value
    ) {

        String normalized =
            basicNormalize(value);

        if (normalized.isBlank()) {

            throw new IllegalArgumentException(
                "normalized email is blank"
            );
        }

        return normalized;
    }


    /**
     * =====================================================
     * PHONE NORMALIZATION
     * =====================================================
     */
    private String normalizePhone(
        String value
    ) {

        String normalized = basicNormalize(value);

        normalized =
            normalized
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace("+", "");

        if (normalized.isBlank()) {

            throw new IllegalArgumentException(
                "normalized phone is blank"
            );
        }

        return normalized;
    }


    /**
     * =====================================================
     * USERNAME NORMALIZATION
     * =====================================================
     */
    private String normalizeUsername(
        String value
    ) {

        String normalized =
            basicNormalize(value);

        if (normalized.isBlank()) {

            throw new IllegalArgumentException(
                "normalized username is blank"
            );
        }

        return normalized;
    }


    /**
     * =====================================================
     * BASIC NORMALIZATION
     * =====================================================
     *
     * Shared normalization:
     *
     * - trim
     * - lowercase
     * - unicode normalize
     *
     * =====================================================
     */
    private String basicNormalize(
        String value
    ) {

        String normalized =
            value
            .trim()
            .toLowerCase(Locale.ROOT);

        // ============================================
        // Unicode normalization
        // ============================================

        normalized =
            Normalizer.normalize(
                normalized,
                Normalizer.Form.NFKC
            );

        return normalized;
    }
}