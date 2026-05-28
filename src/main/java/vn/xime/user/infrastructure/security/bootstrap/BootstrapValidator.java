package vn.xime.user.infrastructure.security.bootstrap;

import java.time.Instant;

import java.util.Objects;


/**
 * =========================================================
 * BOOTSTRAP VALIDATOR
 * =========================================================
 *
 * Responsible for:
 *
 * - bootstrap payload validation
 * - bootstrap certificate validation
 * - startup trust validation
 * - fatal bootstrap security checks
 *
 * This validator only validates:
 *
 * - structural validity
 * - service identity match
 * - expiration
 * - deleted state
 * - required fields
 *
 * Business validation is handled at higher layers.
 *
 * =========================================================
 */
public class BootstrapValidator {

    // =====================================================
    // VALIDATE
    // =====================================================

    public void validate(
        String currentServiceId,
        BootstrapPayload payload
    ) {

        // =================================================
        // PAYLOAD
        // =================================================

        requireNotBlank(
            currentServiceId,
            "Current service ID is missing",
            "Thiếu service ID hiện tại"
        );

        if (payload == null) {

            fatal(
                "Bootstrap payload is null",
                "Bootstrap payload bị null"
            );
        }

        // =================================================
        // CERTIFICATE
        // =================================================

        BootstrapPayload.Certificate certificate =
            payload.certificate();

        if (certificate == null) {

            fatal(
                "Bootstrap certificate is missing",
                "Thiếu bootstrap certificate"
            );
        }

        // =================================================
        // REQUIRED FIELDS
        // =================================================

        requireNotBlank(
            certificate.id(),
            "Bootstrap certificate ID is missing",
            "Thiếu bootstrap certificate ID"
        );

        requireNotBlank(
            certificate.serviceId(),
            "Bootstrap certificate service ID is missing",
            "Thiếu service ID trong bootstrap certificate"
        );

        requireNotBlank(
            certificate.publicCert(),
            "Bootstrap public certificate is missing",
            "Thiếu public certificate"
        );

        requireNotBlank(
            certificate.privateKey(),
            "Bootstrap private key is missing",
            "Thiếu private key"
        );

        requireNotBlank(
            payload.tokenId(),
            "Bootstrap token ID is missing",
            "Thiếu bootstrap token ID"
        );

        requireNotBlank(
            payload.refreshToken(),
            "Bootstrap refresh token is missing",
            "Thiếu bootstrap refresh token"
        );

        // =================================================
        // SERVICE ID MATCH
        // =================================================

        if (!Objects.equals(
            currentServiceId,
            certificate.serviceId()
        )) {

            fatal(
                """
                Bootstrap certificate service ID mismatch.

                Expected:
                %s

                Actual:
                %s
                """.formatted(
                    currentServiceId,
                    certificate.serviceId()
                ),

                """
                Service ID của bootstrap certificate không khớp.

                Mong đợi:
                %s

                Thực tế:
                %s
                """.formatted(
                    currentServiceId,
                    certificate.serviceId()
                )
            );
        }

        // =================================================
        // DELETED
        // =================================================

        if (certificate.deleted()) {

            fatal(
                "Bootstrap certificate has been deleted",
                "Bootstrap certificate đã bị xóa"
            );
        }

        // =================================================
        // STATUS
        // =================================================

        requireNotBlank(
            certificate.status(),
            "Bootstrap certificate status is missing",
            "Thiếu trạng thái bootstrap certificate"
        );

        if (!"ACTIVE".equals(
            certificate.status()
        )) {

            fatal(
                """
                Bootstrap certificate is not ACTIVE.

                Actual status:
                %s
                """.formatted(
                    certificate.status()
                ),

                """
                Bootstrap certificate không ở trạng thái ACTIVE.

                Trạng thái hiện tại:
                %s
                """.formatted(
                    certificate.status()
                )
            );
        }

        // =================================================
        // EXPIRATION
        // =================================================

        Instant now = Instant.now();

        Instant expiresAt =
            certificate.expiresAtInstant();

        if (expiresAt.isBefore(now)) {

            fatal(
                """
                Bootstrap certificate has expired.

                Expires at:
                %s
                """.formatted(
                    expiresAt
                ),

                """
                Bootstrap certificate đã hết hạn.

                Hết hạn lúc:
                %s
                """.formatted(
                    expiresAt
                )
            );
        }

        // =================================================
        // ISSUED TIME
        // =================================================

        Instant issuedAt =
            certificate.issuedAtInstant();

        if (issuedAt.isAfter(now.plusSeconds(60))) {

            fatal(
                """
                Bootstrap certificate issued_at is invalid.

                issued_at:
                %s
                """.formatted(
                    issuedAt
                ),

                """
                issued_at của bootstrap certificate không hợp lệ.

                issued_at:
                %s
                """.formatted(
                    issuedAt
                )
            );
        }
    }

    // =====================================================
    // REQUIRED FIELD
    // =====================================================

    private void requireNotBlank(
        String value,
        String english,
        String vietnamese
    ) {

        if (
            value == null
                || value.isBlank()
        ) {

            fatal(
                english,
                vietnamese
            );
        }
    }

    // =====================================================
    // FATAL
    // =====================================================

    private void fatal(
        String english,
        String vietnamese
    ) {

        String message =
            """
            ==================================================
            FATAL BOOTSTRAP SECURITY ERROR
            ==================================================

            [ ENGLISH ]

            %s


            [ TIẾNG VIỆT ]

            %s


            System startup terminated immediately.

            Hệ thống sẽ dừng khởi động ngay lập tức.

            ==================================================
            """.formatted(
                english,
                vietnamese
            );

        System.err.println(
            message
        );

        throw new IllegalStateException(
            message
        );
    }
}
