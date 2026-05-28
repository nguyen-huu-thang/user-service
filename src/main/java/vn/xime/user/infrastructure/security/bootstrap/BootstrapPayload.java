package vn.xime.user.infrastructure.security.bootstrap;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;


/**
 * =========================================================
 * BOOTSTRAP PAYLOAD
 * =========================================================
 *
 * Base64 encoded JSON payload.
 *
 * Used only for:
 *
 * - first startup
 * - trust bootstrap
 * - initial certificate exchange
 *
 * =========================================================
 */
public record BootstrapPayload(

        Certificate certificate,

        @JsonProperty("token_id")
        String tokenId,

        @JsonProperty("refresh_token")
        String refreshToken
) {

    // =====================================================
    // CERTIFICATE
    // =====================================================

    public record Certificate(

            String id,

            @JsonProperty("service_id")
            String serviceId,

            @JsonProperty("public_cert")
            String publicCert,

            @JsonProperty("private_key")
            String privateKey,

            String status,

            @JsonProperty("issued_at")
            String issuedAt,

            @JsonProperty("expires_at")
            String expiresAt,

            boolean deleted
    ) {

        public Instant issuedAtInstant() {

            return Instant.ofEpochMilli(
                    Long.parseLong(
                            issuedAt
                    )
            );
        }

        public Instant expiresAtInstant() {

            return Instant.ofEpochMilli(
                    Long.parseLong(
                            expiresAt
                    )
            );
        }
    }
}