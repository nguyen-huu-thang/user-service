package vn.xime.user.infrastructure.security.jwt;

import java.security.KeyFactory;
import java.security.PublicKey;

import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;

import java.security.spec.X509EncodedKeySpec;

import java.time.Instant;

import java.util.Base64;
import java.util.Date;

import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import vn.xime.user.domain.authentication.model.KeyContext;
import vn.xime.user.domain.authentication.model.JwtClaims;

import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.port.out.security.JwtTokenVerifier;


/**
 * =========================================================
 * NIMBUS JWT VERIFIER
 * =========================================================
 *
 * Responsibilities:
 *
 * - verify JWT signature
 * - validate JWT time claims
 * - parse JWT claims
 * - convert JWT into domain model
 *
 * KHÔNG:
 *
 * - key lookup
 * - trust synchronization
 * - token issuance
 * - refresh token lifecycle
 * - authorization logic
 *
 * JWT verification is fully local:
 *
 * JWT
 *   ↓
 * extract kid
 *   ↓
 * resolve public key
 *   ↓
 * verify signature
 *
 * No runtime trust-service dependency.
 *
 * =========================================================
 */
@Slf4j
@Component
public class NimbusJwtVerifier implements JwtTokenVerifier {

    /**
     * =====================================================
     * CLOCK SKEW
     * =====================================================
     *
     * Helps tolerate:
     *
     * - clock drift
     * - distributed node time difference
     *
     * =====================================================
     */
    private static final long CLOCK_SKEW_SECONDS =
        30;


    /**
     * =====================================================
     * VERIFY JWT
     * =====================================================
     */
    public JwtClaims verify(
        String token,
        KeyContext keyContext
    ) {

        try {

            SignedJWT signedJwt =
                SignedJWT.parse(
                    token
                );


            // =============================================
            // VERIFY SIGNATURE
            // =============================================

            verifySignature(
                signedJwt,
                keyContext
            );


            // =============================================
            // EXTRACT CLAIMS
            // =============================================

            JWTClaimsSet claims =
                signedJwt.getJWTClaimsSet();


            // =============================================
            // VALIDATE TIME CLAIMS
            // =============================================

            validateTimeClaims(
                claims
            );


            // =============================================
            // MAP DOMAIN
            // =============================================

            return mapClaims(
                claims
            );

        } catch (Exception exception) {

            log.error(
                "Cannot verify JWT: {}",
                exception.getMessage()
            );

            throw new RuntimeException(
                "Cannot verify JWT",
                exception
            );
        }
    }


    /**
     * =====================================================
     * VERIFY SIGNATURE
     * =====================================================
     */
    private void verifySignature(
        SignedJWT signedJwt,
        KeyContext keyContext
    ) throws Exception {

        String keyType =
            keyContext.getKeyType();


        /*
         * =================================================
         * RSA
         * =================================================
         */
        if ("RSA".equalsIgnoreCase(keyType)) {

            RSAPublicKey publicKey =
                parseRsaPublicKey(
                    keyContext.getPublicKey()
                );


            boolean verified =
                signedJwt.verify(

                    new RSASSAVerifier(
                        publicKey
                    )
                );


            if (!verified) {

                throw new RuntimeException(
                    "Invalid JWT signature"
                );
            }

            return;
        }


        /*
         * =================================================
         * EC
         * =================================================
         */
        if ("EC".equalsIgnoreCase(keyType)) {

            ECPublicKey publicKey =
                parseEcPublicKey(
                    keyContext.getPublicKey()
                );


            boolean verified =
                signedJwt.verify(

                    new ECDSAVerifier(
                        publicKey
                    )
                );


            if (!verified) {

                throw new RuntimeException(
                    "Invalid JWT signature"
                );
            }

            return;
        }


        /*
         * =================================================
         * ED25519
         * =================================================
         */
        // TODO:
        // add EdDSA verifier later


        throw new IllegalArgumentException(
            "Unsupported key type: " + keyType
        );
    }


    /**
     * =====================================================
     * VALIDATE TIME CLAIMS
     * =====================================================
     */
    private void validateTimeClaims(
        JWTClaimsSet claims
    ) {

        Instant now =
            Instant.now();


        /*
         * =================================================
         * EXP
         * =================================================
         */
        if (
            claims.getExpirationTime() != null
            &&
            claims.getExpirationTime()
                .toInstant()
                .plusSeconds(
                    CLOCK_SKEW_SECONDS
                )
                .isBefore(now)
        ) {

            throw new RuntimeException(
                "JWT expired"
            );
        }


        /*
         * =================================================
         * NBF
         * =================================================
         */
        if (
            claims.getNotBeforeTime() != null
            &&
            claims.getNotBeforeTime()
                .toInstant()
                .minusSeconds(
                    CLOCK_SKEW_SECONDS
                )
                .isAfter(now)
        ) {

            throw new RuntimeException(
                "JWT not active yet"
            );
        }
    }


    /**
     * =====================================================
     * MAP JWT CLAIMS
     * =====================================================
     */
    private JwtClaims mapClaims(
        JWTClaimsSet claims
    ) throws Exception {

        return new JwtClaims(

            IdService.fromString(
                claims.getJWTID()
            ),

            IdService.fromString(
                claims.getSubject()
            ),

            claims.getIssuer(),

            claims.getAudience() != null
                &&
                !claims.getAudience().isEmpty()

                    ? claims.getAudience()
                        .getFirst()

                    : null,

            toInstant(
                claims.getIssueTime()
            ),

            toInstant(
                claims.getExpirationTime()
            ),

            toInstant(
                claims.getNotBeforeTime()
            ),

            Instant.ofEpochSecond(
                    claims.getLongClaim(
                        "auth_time"
                    )
                ),

            claims.getIntegerClaim(
                "token_version"
            )
        );
    }


    /**
     * =====================================================
     * PARSE RSA PUBLIC KEY
     * =====================================================
     */
    private RSAPublicKey parseRsaPublicKey(
        String publicKey
    ) throws Exception {

        return (RSAPublicKey)

            parsePublicKey(
                publicKey,
                "RSA"
            );
    }


    /**
     * =====================================================
     * PARSE EC PUBLIC KEY
     * =====================================================
     */
    private ECPublicKey parseEcPublicKey(
        String publicKey
    ) throws Exception {

        return (ECPublicKey)

            parsePublicKey(
                publicKey,
                "EC"
            );
    }


    /**
     * =====================================================
     * PARSE PUBLIC KEY
     * =====================================================
     */
    private PublicKey parsePublicKey(
        String publicKey,
        String keyType
    ) throws Exception {

        String normalized =
            publicKey

                .replace(
                    "-----BEGIN PUBLIC KEY-----",
                    ""
                )

                .replace(
                    "-----END PUBLIC KEY-----",
                    ""
                )

                .replaceAll(
                    "\\s",
                    ""
                );


        byte[] keyBytes =
            Base64.getDecoder()
                .decode(
                    normalized
                );


        X509EncodedKeySpec keySpec =
            new X509EncodedKeySpec(
                keyBytes
            );


        KeyFactory keyFactory =
            KeyFactory.getInstance(
                keyType
            );


        return keyFactory.generatePublic(
            keySpec
        );
    }


    /**
     * =====================================================
     * TO INSTANT
     * =====================================================
     */
    private Instant toInstant(
        Date date
    ) {

        return date != null
            ? date.toInstant()
            : null;
    }
}