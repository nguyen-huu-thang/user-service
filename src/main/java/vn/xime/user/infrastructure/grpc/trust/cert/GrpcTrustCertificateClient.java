package vn.xime.user.infrastructure.grpc.trust.cert;

import java.time.Instant;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;


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
 * - grpc transport handling
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
     * ROTATE CERTIFICATE
     * =====================================================
     *
     * Returns:
     *
     * - new certificate
     * - next refresh token
     * - refresh token id
     *
     * =====================================================
     */
    public Certificate rotateCertificate(
        String tokenId,
        String refreshToken,
        String privateKey
    ) {

        ManagedChannel channel = buildChannel();

        try {

            CertificateServiceGrpc
                .CertificateServiceBlockingStub stub =

                    buildStub(
                        channel
                    );


            RotateCertificateRequest request =
                RotateCertificateRequest
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

        } finally {

            channel.shutdown();
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