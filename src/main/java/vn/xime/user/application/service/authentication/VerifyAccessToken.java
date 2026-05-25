package vn.xime.user.application.service.authentication;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import vn.xime.user.domain.authentication.model.JwtClaims;
import vn.xime.user.domain.authentication.model.KeyContext;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.application.port.out.integration.ResolveVerificationKey;

import vn.xime.user.application.port.out.security.JwtTokenVerifier;


/**
 * =========================================================
 * VERIFY ACCESS TOKEN
 * =========================================================
 *
 * Application service responsible for:
 *
 * - extracting JWT key id (kid)
 * - resolving verification key
 * - verifying JWT
 * - returning verified JWT claims
 *
 * This service KHÔNG:
 *
 * - verify cryptographic signature directly
 * - use Nimbus JOSE APIs directly
 * - call trust-service directly
 * - manage JWT lifecycle
 * - manage refresh token flow
 *
 * Cryptographic logic belongs to infrastructure.
 *
 * =========================================================
 */
@Service
@RequiredArgsConstructor
public class VerifyAccessToken {

    /**
     * =====================================================
     * VERIFICATION KEY RESOLVER
     * =====================================================
     */
    private final ResolveVerificationKey
        resolveVerificationKey;


    /**
     * =====================================================
     * JWT VERIFIER
     * =====================================================
     */
    private final JwtTokenVerifier
        jwtVerifier;


    /**
     * =====================================================
     * JSON PARSER
     * =====================================================
     *
     * Temporary local implementation.
     *
     * Later may move to:
     *
     * JwtHeaderExtractor abstraction.
     *
     * =====================================================
     */
    private final ObjectMapper objectMapper =
        new ObjectMapper();


    /**
     * =====================================================
     * VERIFY ACCESS TOKEN
     * =====================================================
     */
    public JwtClaims execute(
        String accessToken
    ) {

        /*
         * =================================================
         * 1. EXTRACT KEY ID
         * =================================================
         */

        Id keyId =
            extractKeyId(
                accessToken
            );


        /*
         * =================================================
         * 2. RESOLVE VERIFICATION KEY
         * =================================================
         */

        KeyContext keyContext =

            resolveVerificationKey
                .resolve(
                    keyId
                )

                .orElseThrow(() ->

                    new RuntimeException(
                        "verification key not found: "
                            + keyId
                    )
                );


        /*
         * =================================================
         * 3. VERIFY JWT
         * =================================================
         */

        return jwtVerifier.verify(
            accessToken,
            keyContext
        );
    }


    /**
     * =====================================================
     * EXTRACT JWT KEY ID (kid)
     * =====================================================
     *
     * Current implementation:
     *
     * - parse base64url header
     * - decode JSON
     * - extract "kid"
     *
     * Signature is NOT verified at this step.
     *
     * =====================================================
     */
    private Id extractKeyId(
        String token
    ) {

        try {

            /*
             * =============================================
             * JWT FORMAT
             * =============================================
             *
             * header.payload.signature
             *
             * =============================================
             */

            String[] parts =
                token.split("\\.");

            if (parts.length != 3) {

                throw new RuntimeException(
                    "Invalid JWT format"
                );
            }


            /*
             * =============================================
             * HEADER
             * =============================================
             */

            String encodedHeader =
                parts[0];


            byte[] decodedBytes =

                Base64.getUrlDecoder()
                    .decode(
                        encodedHeader
                    );


            String headerJson =
                new String(

                    decodedBytes,

                    StandardCharsets.UTF_8
                );


            JsonNode header =

                objectMapper.readTree(
                    headerJson
                );


            /*
             * =============================================
             * KID
             * =============================================
             */

            JsonNode keyIdNode =
                header.get(
                    "kid"
                );

            if (keyIdNode == null) {

                throw new RuntimeException(
                    "JWT kid missing"
                );
            }


            String keyId =
                keyIdNode.asText();


            return IdService.fromString(
                keyId
            );

        } catch (Exception exception) {

            throw new RuntimeException(
                "Cannot extract JWT key id",
                exception
            );
        }
    }
}