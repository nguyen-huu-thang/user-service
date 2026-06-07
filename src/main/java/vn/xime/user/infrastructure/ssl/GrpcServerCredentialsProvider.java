package vn.xime.user.infrastructure.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.grpc.ServerCredentials;
import io.grpc.TlsServerCredentials;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.cert.TrustCertificateResolver;
import vn.xime.user.integration.trust.model.Certificate;
import vn.xime.user.integration.trust.publicca.TrustRootCertificateResolver;


/**
 * =========================================================
 * GRPC SERVER CREDENTIALS PROVIDER
 * =========================================================
 *
 * Builds TlsServerCredentials for the custom gRPC server
 * (port 9092). Requires connecting clients to present a
 * valid certificate (mTLS).
 *
 * Reads User Service's own cert from TrustCertificateResolver
 * (loaded from Trust Service at bootstrap).
 * Reads root CA from TrustRootCertificateResolver.
 *
 * Called once when Spring creates the gRPC server bean,
 * after cert loaders have populated the resolvers.
 *
 * =========================================================
 * WHY CUSTOM NETTYSERVERBUILDER
 * =========================================================
 *
 * Spring gRPC 1.0.2 builds TLS via:
 *   spring.grpc.server.ssl -> SslBundle -> KeyManagerFactory
 *
 * SslBundle is designed for file-based certs (PEM files).
 * User Service stores certs in database (via Trust Service).
 * These two models are incompatible.
 *
 * Solution: bypass Spring gRPC auto-configuration entirely.
 * Use NettyServerBuilder directly with TlsServerCredentials
 * built from the in-memory cert resolvers.
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class GrpcServerCredentialsProvider {

    private static final Logger log =
        LoggerFactory.getLogger(GrpcServerCredentialsProvider.class);

    private final TrustCertificateResolver trustCertificateResolver;
    private final TrustRootCertificateResolver trustRootCertificateResolver;


    /**
     * =====================================================
     * BUILD SERVER CREDENTIALS
     * =====================================================
     *
     * Called once when GrpcServerConfig creates the Server bean.
     * Cert resolvers must be populated before this is called
     * (enforced via @DependsOn on the server bean).
     *
     * =====================================================
     */
    public ServerCredentials buildServerCredentials() throws IOException {

        log.info("Building gRPC server mTLS credentials");

        Certificate certificate =
            trustCertificateResolver
                .resolve()
                .orElseThrow(() -> new IllegalStateException(
                    "Runtime certificate not found — cert loader has not run yet"
                ));

        String rootCaPem =
            trustRootCertificateResolver
                .resolve()
                .orElseThrow(() -> new IllegalStateException(
                    "Root CA certificate not found — root cert loader has not run yet"
                ));

        ServerCredentials credentials = TlsServerCredentials.newBuilder()
            .keyManager(
                certInputStream(certificate.publicCertificate()),
                privateKeyInputStream(certificate.privateKey())
            )
            .trustManager(
                toStream(rootCaPem)
            )
            .clientAuth(TlsServerCredentials.ClientAuth.REQUIRE)
            .build();

        log.info("gRPC server mTLS credentials built successfully (clientAuth=REQUIRE)");

        return credentials;
    }


    /**
     * =====================================================
     * CERT INPUT STREAM
     * =====================================================
     *
     * Trust Service sends cert as raw base64 without PEM
     * headers. TlsServerCredentials requires PEM format.
     * Wrap if headers are missing.
     *
     * =====================================================
     */
    private InputStream certInputStream(String value) {

        String pem = value.startsWith("-----")
            ? value
            : "-----BEGIN CERTIFICATE-----\n" + value + "\n-----END CERTIFICATE-----\n";

        return toStream(pem);
    }


    /**
     * =====================================================
     * PRIVATE KEY INPUT STREAM
     * =====================================================
     *
     * Trust Service sends private key as raw base64 PKCS#8.
     * TlsServerCredentials requires PEM format.
     * Wrap if headers are missing.
     *
     * =====================================================
     */
    private InputStream privateKeyInputStream(String value) {

        String pem = value.startsWith("-----")
            ? value
            : "-----BEGIN PRIVATE KEY-----\n" + value + "\n-----END PRIVATE KEY-----\n";

        return toStream(pem);
    }


    private InputStream toStream(String value) {

        return new ByteArrayInputStream(
            value.getBytes(StandardCharsets.UTF_8)
        );
    }
}
