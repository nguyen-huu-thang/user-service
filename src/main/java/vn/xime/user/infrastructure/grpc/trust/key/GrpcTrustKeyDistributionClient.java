package vn.xime.user.infrastructure.grpc.trust.key;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import vn.xime.user.domain.authentication.model.KeyContext;

// =====================================================
// GENERATED gRPC
// =====================================================

import vn.xime.trust.grpc.external.key.KeyDistributionServiceGrpc;

import vn.xime.trust.grpc.external.key.GetPublicKeysRequest;
import vn.xime.trust.grpc.external.key.GetPublicKeysResponse;

import vn.xime.trust.grpc.external.key.PublicKeyDto;


/**
 * =========================================================
 * gRPC TRUST KEY DISTRIBUTION CLIENT
 * =========================================================
 *
 * Low-level transport adapter.
 *
 * Responsibilities:
 *
 * - grpc communication
 * - protobuf request mapping
 * - protobuf response mapping
 * - network exception mapping
 * - grpc transport handling
 *
 * KHÔNG:
 *
 * - JWT verification
 * - runtime cache logic
 * - scheduler logic
 * - trust orchestration
 * - signing key management
 *
 * User-service only consumes:
 *
 * - public verification keys
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class GrpcTrustKeyDistributionClient {

    /**
     * =====================================================
     * TRUST SERVICE HOST
     * =====================================================
     */
    private static final String HOST =
        "localhost";


    /**
     * =====================================================
     * TRUST SERVICE PORT
     * =====================================================
     */
    private static final int PORT =
        9090;


    /**
     * =====================================================
     * GET PUBLIC KEYS
     * =====================================================
     *
     * Verification flow.
     *
     * Returns:
     *
     * - public verification keys
     *
     * =====================================================
     */
    public List<KeyContext> getPublicKeys(
        String verifierServiceId
    ) {

        ManagedChannel channel = buildChannel();

        try {

            KeyDistributionServiceGrpc
                .KeyDistributionServiceBlockingStub stub =

                    buildStub(channel);


            GetPublicKeysRequest request =
                GetPublicKeysRequest
                    .newBuilder()

                    .setVerifierServiceId(
                        verifierServiceId
                    )

                    .build();


            GetPublicKeysResponse response =
                stub.getPublicKeys(
                    request
                );


            return response
                .getKeysList()
                .stream()

                .map(
                    this::mapPublicKey
                )

                .toList();

        } catch (StatusRuntimeException exception) {

            throw new RuntimeException(
                "trust-service public key request failed",
                exception
            );
        }
    }


    /**
     * =====================================================
     * BUILD CHANNEL
     * =====================================================
     */
    private ManagedChannel buildChannel() {

        return ManagedChannelBuilder
            .forAddress(
                HOST,
                PORT
            )

            .usePlaintext()

            .build();
    }


    /**
     * =====================================================
     * BUILD STUB
     * =====================================================
     */
    private KeyDistributionServiceGrpc
        .KeyDistributionServiceBlockingStub buildStub(ManagedChannel channel) {

        return KeyDistributionServiceGrpc
            .newBlockingStub(
                channel
            );
    }


    /**
     * =====================================================
     * MAP PUBLIC KEY
     * =====================================================
     */
    private KeyContext mapPublicKey(
        PublicKeyDto dto
    ) {

        return new KeyContext(

            dto.getId(),

            dto.getAlgorithm(),

            resolveKeySpec(
                dto.getAlgorithm(),
                dto.getKeySize()
            ),

            dto.getVerifierServiceId(),

            dto.getPublicKey(),

            Instant.ofEpochMilli(
                dto.getActivateAt()
            ),

            Instant.ofEpochMilli(
                dto.getExpiresAt()
            )
        );
    }


    /**
     * =====================================================
     * RESOLVE KEY SPEC
     * =====================================================
     */
    private String resolveKeySpec(
        String algorithm,
        int keySize
    ) {

        return switch (algorithm) {

            case "EC" -> switch (keySize) {

                case 256 -> "P-256";

                case 384 -> "P-384";

                case 521 -> "P-521";

                default -> "UNKNOWN";
            };

            case "RSA" -> String.valueOf(
                keySize
            );

            default -> "UNKNOWN";
        };
    }
}