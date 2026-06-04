package vn.xime.user.config.grpc;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundleKey;
import org.springframework.boot.ssl.SslBundleRegistry;
import org.springframework.boot.ssl.SslManagerBundle;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.boot.ssl.SslStoreBundle;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import org.springframework.grpc.server.ServerBuilderCustomizer;

import vn.xime.user.integration.trust.cert.TrustCertificateResolver;
import vn.xime.user.integration.trust.model.Certificate;
import vn.xime.user.integration.trust.publicca.TrustRootCertificateResolver;


/**
 * =========================================================
 * MTLS SSL BUNDLE REGISTRAR
 * =========================================================
 *
 * Spring gRPC 1.0.2 dùng Spring Boot SslBundles API để cấu
 * hình TLS. ShadedNettyGrpcServerFactory nhận KeyManagerFactory
 * và TrustManagerFactory được trích xuất từ SslBundle.
 *
 * Class này đăng ký bundle "user-grpc-mtls" vào SslBundleRegistry
 * trước khi ShadedNettyGrpcServerFactory được khởi tạo.
 *
 * =========================================================
 * DEPENDENCY CHAIN (đảm bảo đúng thứ tự)
 * =========================================================
 *
 * Bean này implement ServerBuilderCustomizer<NettyServerBuilder>
 * (no-op), khiến ShadedNettyGrpcServerFactory phải đợi bean này
 * được tạo xong (thông qua ServerBuilderCustomizers). Như vậy
 * @PostConstruct (đăng ký SSL bundle) chạy trước khi factory
 * đọc SslBundles.
 *
 * @DependsOn("trustCertificateSynchronizationScheduler") đảm bảo
 * scheduler đã load cert vào resolver trước khi @PostConstruct
 * của bean này chạy.
 *
 * =========================================================
 */
@Component
@DependsOn({ "trustCertificateSynchronizationScheduler", "trustRootCertificateInitializer" })
public class MtlsSslBundleRegistrar implements ServerBuilderCustomizer<NettyServerBuilder> {

    static final String BUNDLE_NAME = "user-grpc-mtls";

    private static final Logger log = LoggerFactory.getLogger(MtlsSslBundleRegistrar.class);

    private final SslBundleRegistry sslBundleRegistry;
    private final TrustCertificateResolver trustCertificateResolver;
    private final TrustRootCertificateResolver trustRootCertificateResolver;


    public MtlsSslBundleRegistrar(
        SslBundleRegistry sslBundleRegistry,
        TrustCertificateResolver trustCertificateResolver,
        TrustRootCertificateResolver trustRootCertificateResolver
    ) {
        this.sslBundleRegistry = sslBundleRegistry;
        this.trustCertificateResolver = trustCertificateResolver;
        this.trustRootCertificateResolver = trustRootCertificateResolver;
    }


    /**
     * Register mTLS SSL bundle into Spring Boot's SslBundleRegistry.
     * Runs after trustCertificateSynchronizationScheduler has loaded the cert.
     * Runs before ShadedNettyGrpcServerFactory reads SslBundles.
     */
    @PostConstruct
    public void registerMtlsBundle() {

        log.info("Registering mTLS SSL bundle: {}", BUNDLE_NAME);

        Certificate certificate =
            trustCertificateResolver
                .resolve()
                .orElseThrow(
                    () -> new IllegalStateException(
                        "runtime certificate not found — scheduler chưa load cert"
                    )
                );

        String rootCertificate =
            trustRootCertificateResolver
                .resolve()
                .orElseThrow(
                    () -> new IllegalStateException(
                        "root certificate not found — ca-cert chưa được load"
                    )
                );

        try {

            KeyStore keyStore =
                buildKeyStore(
                    certificate.publicCertificate(),
                    certificate.privateKey()
                );

            KeyStore trustStore =
                buildTrustStore(rootCertificate);

            SslStoreBundle storeBundle =
                SslStoreBundle.of(keyStore, "", trustStore);

            // Use SunX509 algorithm for both factories.
            // SunX509 uses SimpleValidator (not PKIX) so it does NOT enforce
            // cA=TRUE on trust anchors — necessary because Trust Service issues
            // root CA certs without the Basic Constraints CA flag.
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, new char[0]);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(trustStore);

            SslManagerBundle managers = SslManagerBundle.of(kmf, tmf);

            SslBundle bundle = SslBundle.of(
                storeBundle,
                SslBundleKey.NONE,
                SslOptions.NONE,
                "TLS",
                managers
            );

            sslBundleRegistry.registerBundle(BUNDLE_NAME, bundle);

            log.info("mTLS SSL bundle registered: {}", BUNDLE_NAME);

        } catch (Exception exception) {

            throw new IllegalStateException(
                "Failed to register mTLS SSL bundle: " + BUNDLE_NAME,
                exception
            );
        }
    }


    /**
     * No-op — exists only to create a dependency chain that forces
     * this bean to be created before ShadedNettyGrpcServerFactory.
     */
    @Override
    public void customize(NettyServerBuilder builder) {
    }


    /**
     * Build a KeyStore containing the server certificate and private key.
     * Wraps raw base64 DER in PEM headers if missing.
     * Tries RSA then EC key algorithms.
     */
    private KeyStore buildKeyStore(
        String publicCertificate,
        String privateKey
    ) throws Exception {

        String certPem = publicCertificate.startsWith("-----")
            ? publicCertificate
            : "-----BEGIN CERTIFICATE-----\n" + publicCertificate + "\n-----END CERTIFICATE-----\n";

        String keyPem = privateKey.startsWith("-----")
            ? privateKey
            : "-----BEGIN PRIVATE KEY-----\n" + privateKey + "\n-----END PRIVATE KEY-----\n";

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        X509Certificate cert = (X509Certificate) cf.generateCertificate(
            new ByteArrayInputStream(certPem.getBytes(StandardCharsets.UTF_8))
        );

        byte[] keyBytes = extractDerFromPem(keyPem);
        PrivateKey pk = parsePrivateKey(keyBytes);

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(null, null);
        keyStore.setKeyEntry(
            "server",
            pk,
            new char[0],
            new java.security.cert.Certificate[]{ cert }
        );

        return keyStore;
    }


    /**
     * Build a trust KeyStore containing the root CA certificate.
     */
    private KeyStore buildTrustStore(
        String rootCaPem
    ) throws Exception {

        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        X509Certificate rootCa = (X509Certificate) cf.generateCertificate(
            new ByteArrayInputStream(rootCaPem.getBytes(StandardCharsets.UTF_8))
        );

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(null, null);
        trustStore.setCertificateEntry("root-ca", rootCa);

        return trustStore;
    }


    /**
     * Extract raw DER bytes from PEM string.
     */
    private byte[] extractDerFromPem(String pem) {

        String base64 = pem
            .replaceAll("-----BEGIN [^-]+-----", "")
            .replaceAll("-----END [^-]+-----", "")
            .replaceAll("\\s+", "");

        return Base64.getDecoder().decode(base64);
    }


    /**
     * Parse PKCS#8 private key — tries RSA then EC.
     * Algorithm OID is embedded in the DER structure.
     */
    private PrivateKey parsePrivateKey(byte[] pkcs8Bytes) throws Exception {

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8Bytes);

        for (String algorithm : new String[]{ "RSA", "EC" }) {
            try {
                return KeyFactory.getInstance(algorithm).generatePrivate(spec);
            } catch (Exception ignored) {
            }
        }

        throw new InvalidKeySpecException(
            "Cannot parse private key — tried RSA and EC algorithms"
        );
    }
}
