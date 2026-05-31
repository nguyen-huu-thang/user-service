package vn.xime.user.infrastructure.grpc.trust.cert;

import java.time.Instant;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;

import vn.xime.user.infrastructure.grpc.channel.GrpcChannelProvider;

import vn.xime.user.integration.trust.model.Certificate;

// =====================================================
// GENERATED gRPC
// =====================================================

import vn.xime.trust.grpc.external.certificate.CertificateServiceGrpc;

import vn.xime.trust.grpc.external.certificate.RotateCertificateRequest;
import vn.xime.trust.grpc.external.certificate.RotateCertificateResponse;


/**
 * =========================================================
 * gRPC TRUST CERTIFICATE CLIENT
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
 *
 * KHÔNG:
 *
 * - bootstrap orchestration
 * - certificate rotation policy
 * - certificate persistence
 * - trust store management
 * - scheduler logic
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class GrpcTrustCertificateClient {

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
     * ROTATE CERTIFICATE
     * =====================================================
     */
    public Certificate rotateCertificate(
        String tokenId,
        String refreshToken,
        String privateKey
    ) {

        ManagedChannel channel =
            channelProvider.getChannel(
                HOST,
                PORT
            );

        try {

            CertificateServiceGrpc
                .CertificateServiceBlockingStub stub =

                    buildStub(
                        channel
                    );


            RotateCertificateRequest request =
                buildRequest(
                    tokenId,
                    refreshToken,
                    privateKey
                );


            RotateCertificateResponse response =
                stub.rotateCertificate(
                    request
                );


            return mapResponse(
                response
            );

        } catch (StatusRuntimeException exception) {

            throw new RuntimeException(
                "trust-service certificate rotation failed",
                exception
            );
        }
    }


    /**
     * =====================================================
     * BUILD STUB
     * =====================================================
     */
    private CertificateServiceGrpc
        .CertificateServiceBlockingStub buildStub(
            ManagedChannel channel
        ) {

        return CertificateServiceGrpc
            .newBlockingStub(
                channel
            );
    }


    /**
     * =====================================================
     * BUILD REQUEST
     * =====================================================
     */
    private RotateCertificateRequest buildRequest(
        String tokenId,
        String refreshToken,
        String privateKey
    ) {

        return RotateCertificateRequest
            .newBuilder()

            .setTokenId(
                tokenId
            )

            .setRefreshToken(
                refreshToken
            )

            .setPrivateKey(
                privateKey
            )

            .build();
    }


    /**
     * =====================================================
     * MAP RESPONSE
     * =====================================================
     */
    private Certificate mapResponse(
        RotateCertificateResponse response
    ) {

        return new Certificate(

            response.getCertificate().getId(),

            response.getCertificate().getPublicCert(),

            response.getCertificate().getPrivateKey(),

            response.getServiceId(),

            response.getRefreshTokenId(),

            response.getNextRefreshToken(),

            Instant.ofEpochMilli(
                response.getIssuedAt()
            ),

            Instant.ofEpochMilli(
                response.getExpiresAt()
            )
        );
    }
}