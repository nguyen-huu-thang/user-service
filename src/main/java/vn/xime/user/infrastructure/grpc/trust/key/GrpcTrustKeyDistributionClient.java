package vn.xime.user.infrastructure.grpc.trust.key;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import vn.xime.user.domain.authentication.model.KeyContext;

import vn.xime.user.infrastructure.grpc.channel.GrpcChannelProvider;


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
    private static final String HOST = "localhost";


    /**
     * =====================================================
     * TRUST SERVICE PORT
     * =====================================================
     */
    private static final int PORT = 9090;


    /**
     * =====================================================
     * CHANNEL PROVIDER
     * =====================================================
     */
    private final GrpcChannelProvider channelProvider;


    /**
     * =====================================================
     * GET PUBLIC KEYS
     * =====================================================
     */
    public List<KeyContext> getPublicKeys(
        String verifierServiceId
    ) {

        ManagedChannel channel =
            channelProvider.getChannel(
                HOST,
                PORT
            );

        try {

            KeyDistributionServiceGrpc
                .KeyDistributionServiceBlockingStub stub =

                    buildStub(
                        channel
                    );


            GetPublicKeysResponse response =
                stub.getPublicKeys(
                    buildRequest(
                        verifierServiceId
                    )
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
     * BUILD STUB
     * =====================================================
     */
    private KeyDistributionServiceGrpc
        .KeyDistributionServiceBlockingStub buildStub(
            ManagedChannel channel
        ) {

        return KeyDistributionServiceGrpc
            .newBlockingStub(
                channel
            );
    }


    /**
     * =====================================================
     * BUILD REQUEST
     * =====================================================
     */
    private GetPublicKeysRequest buildRequest(
        String verifierServiceId
    ) {

        return GetPublicKeysRequest
            .newBuilder()

            .setVerifierServiceId(
                verifierServiceId
            )

            .build();
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