package vn.xime.user.config.grpc;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;

import vn.xime.user.integration.trust.cert.TrustCertificateResolver;
import vn.xime.user.integration.trust.model.Certificate;
import vn.xime.user.integration.trust.publicca.TrustRootCertificateResolver;


/**
 * =========================================================
 * GRPC SERVER mTLS CONFIGURATION
 * =========================================================
 *
 * Responsibilities:
 *
 * - cung cấp ServerCredentials bean cho Spring gRPC
 * - Spring gRPC dùng bean này thay vì InsecureServerCredentials
 *   mặc định (qua @ConditionalOnMissingBean)
 *
 * KHÔNG:
 *
 * - quản lý certificate
 * - quản lý channel
 * - handle gRPC request
 *
 * =========================================================
 * VÌ SAO DÙNG TlsServerCredentials THAY VÌ SslContext
 * =========================================================
 *
 * Spring gRPC 1.0.x tạo NettyServerBuilder.forPort(port, credentials)
 * bằng ServerCredentials API mới của gRPC Java.
 * Một khi builder đã có credentials, gọi .sslContext() sẽ throw
 * "Cannot change security when using ServerCredentials".
 * Phải configure security qua ServerCredentials từ đầu.
 *
 * =========================================================
 * LIFECYCLE
 * =========================================================
 *
 * @DependsOn("trustCertificateSynchronizationScheduler") đảm bảo
 * scheduler đã chạy @PostConstruct (load cert vào resolver)
 * trước khi bean này được tạo.
 *
 * =========================================================
 */
// @Configuration removed — Spring gRPC 1.0.2 does not use @Bean ServerCredentials.
// Replaced by GrpcServerConfig + GrpcServerCredentialsProvider (Custom NettyServerBuilder).
public class GrpcServerMtlsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GrpcServerMtlsConfiguration.class);

    /**
     * =====================================================
     * GRPC SERVER mTLS CREDENTIALS
     * =====================================================
     *
     * Spring gRPC pick up bean này qua
     * @ConditionalOnMissingBean(ServerCredentials.class)
     * trong auto-configuration.
     *
     * =====================================================
     */
    @Bean
    @DependsOn({ "trustCertificateSynchronizationScheduler", "trustRootCertificateInitializer" })
    ServerCredentials grpcServerCredentials(
        TrustCertificateResolver trustCertificateResolver,
        TrustRootCertificateResolver trustRootCertificateResolver
    ) throws IOException {

        log.info("Creating gRPC server mTLS credentials");

        Certificate certificate =
            trustCertificateResolver
                .resolve()
                .orElseThrow(
                    () -> new IllegalStateException(
                        "runtime certificate not found"
                    )
                );

        String rootCertificate =
            trustRootCertificateResolver
                .resolve()
                .orElseThrow(
                    () -> new IllegalStateException(
                        "root certificate not found"
                    )
                );

        ServerCredentials credentials = TlsServerCredentials
            .newBuilder()

            .keyManager(
                certInputStream(
                    certificate.publicCertificate()
                ),
                privateKeyInputStream(
                    certificate.privateKey()
                )
            )

            .trustManager(
                inputStream(
                    rootCertificate
                )
            )

            .clientAuth(
                TlsServerCredentials.ClientAuth.REQUIRE
            )

            .build();

        log.info("gRPC server mTLS credentials created successfully (clientAuth=REQUIRE)");

        return credentials;
    }


    /**
     * =====================================================
     * CERT INPUT STREAM
     * =====================================================
     *
     * Trust Service gửi cert dưới dạng raw base64.
     * TlsServerCredentials yêu cầu PEM format.
     * Trust Service sends cert as raw base64.
     * TlsServerCredentials requires PEM format.
     *
     * =====================================================
     */
    private InputStream certInputStream(
        String value
    ) {

        String pem = value.startsWith("-----")
            ? value
            : "-----BEGIN CERTIFICATE-----\n"
                + value
                + "\n-----END CERTIFICATE-----\n";

        return new ByteArrayInputStream(
            pem.getBytes(
                StandardCharsets.UTF_8
            )
        );
    }


    /**
     * =====================================================
     * PRIVATE KEY INPUT STREAM
     * =====================================================
     *
     * Trust Service gửi private key dưới dạng raw base64
     * PKCS#8. TlsServerCredentials yêu cầu PEM format.
     * Trust Service sends private key as raw base64 PKCS#8.
     * TlsServerCredentials requires PEM format.
     *
     * =====================================================
     */
    private InputStream privateKeyInputStream(
        String value
    ) {

        String pem = value.startsWith("-----")
            ? value
            : "-----BEGIN PRIVATE KEY-----\n"
                + value
                + "\n-----END PRIVATE KEY-----\n";

        return new ByteArrayInputStream(
            pem.getBytes(
                StandardCharsets.UTF_8
            )
        );
    }


    /**
     * =====================================================
     * INPUT STREAM
     * =====================================================
     *
     * Dùng cho root certificate — đã có PEM headers sẵn.
     *
     * =====================================================
     */
    private InputStream inputStream(
        String value
    ) {

        return new ByteArrayInputStream(
            value.getBytes(
                StandardCharsets.UTF_8
            )
        );
    }
}
