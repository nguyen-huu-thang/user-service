package vn.xime.user.integration.trust.ssl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.cert.TrustCertificateResolver;
import vn.xime.user.integration.trust.model.Certificate;
import vn.xime.user.integration.trust.publicca.TrustRootCertificateResolver;


/**
 * =========================================================
 * TRUST SSL CONTEXT PROVIDER
 * =========================================================
 *
 * Responsibilities:
 *
 * - build mTLS client SSL context
 * - cache SSL context in RAM
 * - provide runtime SSL context
 * - rebuild SSL context when certificate changes
 *
 * KHÔNG:
 *
 * - grpc channel management
 * - certificate persistence
 * - certificate synchronization
 * - certificate rotation scheduling
 *
 * TLS Provider:
 *
 * - JDK SSL (JSSE)
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class TrustSslContextProvider {

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
     * GET SSL CONTEXT
     * =====================================================
     */
    public SslContext getSslContext() {

        SslContext current =
            sslContext;

        if (current != null) {
            return current;
        }

        synchronized (this) {

            if (sslContext == null) {
                sslContext =
                    buildSslContext();
            }

            return sslContext;
        }
    }


    /**
     * =====================================================
     * RELOAD SSL CONTEXT
     * =====================================================
     */
    public synchronized void reload() {

        sslContext = buildSslContext();
    }


    /**
     * =====================================================
     * BUILD SSL CONTEXT
     * =====================================================
     */
    private SslContext buildSslContext() {

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
                .forClient()

                .trustManager(
                    inputStream(
                        rootCertificate
                    )
                )

                .keyManager(
                    certInputStream(
                        certificate.publicCertificate()
                    ),
                    privateKeyInputStream(
                        certificate.privateKey()
                    )
                )

                .build();

        } catch (Exception exception) {

            throw new IllegalStateException(
                "failed to build ssl context",
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