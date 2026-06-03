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
                    inputStream(
                        certificate.publicCertificate()
                    ),
                    inputStream(
                        certificate.privateKey()
                    )
                )

                .sslProvider(
                    SslProvider.JDK
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
