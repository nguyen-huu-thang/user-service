package vn.xime.user.integration.trust.ssl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslProvider;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.cert.TrustCertificateResolver;
import vn.xime.user.integration.trust.model.Certificate;
import vn.xime.user.integration.trust.publicca.TrustRootCertificateResolver;


/**
 * =========================================================
 * GRPC SERVER SSL CONTEXT PROVIDER
 * =========================================================
 *
 * Responsibilities:
 *
 * - build mTLS server SSL context
 * - cache SSL context in RAM
 * - provide runtime SSL context
 * - rebuild SSL context when certificate changes
 *
 * KHÔNG:
 *
 * - grpc server lifecycle management
 * - certificate persistence
 * - certificate synchronization
 * - certificate rotation scheduling
 *
 * TLS Provider:
 *
 * - JDK SSL (JSSE)
 *
 * =========================================================
 * CLIENT AUTH
 * =========================================================
 *
 * ClientAuth.REQUIRE — caller phải present cert hợp lệ
 * do cùng CA ký. Không có cert → reject ngay ở tầng TLS,
 * không vào đến gRPC handler.
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class GrpcServerSslContextProvider {

    /**
     * =====================================================
     * CERTIFICATE RESOLVER
     * =====================================================
     */
    private final TrustCertificateResolver trustCertificateResolver;

    /**
     * =====================================================
     * ROOT CERTIFICATE RESOLVER
     * =====================================================
     */
    private final TrustRootCertificateResolver trustRootCertificateResolver;


    /**
     * =====================================================
     * SSL CONTEXT CACHE
     * =====================================================
     */
    private volatile SslContext sslContext;


    /**
     * =====================================================
     * GET SERVER SSL CONTEXT
     * =====================================================
     */
    public SslContext getServerSslContext() {

        SslContext current =
            sslContext;

        if (current != null) {
            return current;
        }

        synchronized (this) {

            if (sslContext == null) {
                sslContext =
                    buildServerSslContext();
            }

            return sslContext;
        }
    }


    /**
     * =====================================================
     * RELOAD SERVER SSL CONTEXT
     * =====================================================
     *
     * Dùng khi cert mới được rotate.
     * Connection đang giữ không bị ảnh hưởng —
     * chỉ connection mới sẽ dùng context mới.
     *
     * =====================================================
     */
    public synchronized void reload() {

        sslContext = buildServerSslContext();
    }


    /**
     * =====================================================
     * BUILD SERVER SSL CONTEXT
     * =====================================================
     */
    private SslContext buildServerSslContext() {

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

        try {

            return GrpcSslContexts

                // forServer: cert vào constructor, khác với forClient dùng keyManager()
                // forServer: cert goes into constructor, unlike forClient which uses keyManager()
                .forServer(
                    certInputStream(
                        certificate.publicCertificate()
                    ),
                    privateKeyInputStream(
                        certificate.privateKey()
                    )
                )

                // JDK không hỗ trợ NPN_AND_ALPN mà gRPC server yêu cầu.
                // BoringSSL (bundle sẵn trong grpc-netty-shaded) hỗ trợ cả hai.
                // JDK does not support NPN_AND_ALPN required by gRPC server.
                // BoringSSL (bundled in grpc-netty-shaded) supports both.
                .sslProvider(
                    SslProvider.OPENSSL
                )

                .trustManager(
                    inputStream(
                        rootCertificate
                    )
                )

                .clientAuth(
                    ClientAuth.REQUIRE
                )

                .build();

        } catch (Exception exception) {

            throw new IllegalStateException(
                "failed to build server ssl context",
                exception
            );
        }
    }


    /**
     * =====================================================
     * INPUT STREAM
     * =====================================================
     *
     * Dùng cho root certificate — Trust Service gửi
     * root cert đã có PEM headers sẵn.
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


    /**
     * =====================================================
     * CERT INPUT STREAM
     * =====================================================
     *
     * Trust Service gửi service cert dưới dạng raw base64
     * (không có PEM headers). Netty yêu cầu PEM format.
     * Trust Service sends service cert as raw base64
     * (no PEM headers). Netty requires PEM format.
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
     * PKCS#8. Netty yêu cầu PEM format.
     * Trust Service sends private key as raw base64
     * PKCS#8. Netty requires PEM format.
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
}
