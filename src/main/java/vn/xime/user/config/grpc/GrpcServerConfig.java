package vn.xime.user.config.grpc;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import vn.xime.user.api.grpc.identity.LoginGrpcApi;
import vn.xime.user.api.grpc.identity.RegistrationGrpcApi;
import vn.xime.user.infrastructure.ssl.GrpcServerCredentialsProvider;


/**
 * =========================================================
 * GRPC SERVER — mTLS (Custom NettyServerBuilder)
 * =========================================================
 *
 * Starts the gRPC server that Identity Service connects to.
 *
 * Port: 9092
 * Security: mTLS (ClientAuth.REQUIRE)
 *
 * Services:
 * - LoginGrpcApi      — verifyCredential (Identity -> User)
 * - RegistrationGrpcApi — registerUser   (Identity -> User)
 *
 * =========================================================
 * WHY NOT SPRING GRPC AUTO-CONFIGURATION
 * =========================================================
 *
 * Spring gRPC 1.0.2 TLS pipeline:
 *   spring.grpc.server.ssl -> SslBundle -> KeyManagerFactory
 *
 * SslBundle is file-based. User Service certs come from the
 * Trust Service database (not PEM files on disk). The two
 * models are incompatible, so we bypass Spring gRPC's
 * auto-configuration and use NettyServerBuilder directly —
 * the same pattern used by Trust Service.
 *
 * =========================================================
 * LIFECYCLE
 * =========================================================
 *
 * @DependsOn ensures cert loaders finish before this bean
 * is created and calls credentialsProvider.buildServerCredentials().
 *
 * initMethod="start"    — Netty starts listening after Spring context is ready.
 * destroyMethod="shutdown" — graceful shutdown on context close.
 *
 * =========================================================
 */
@Configuration
public class GrpcServerConfig {

    /**
     * =====================================================
     * GRPC SERVER BEAN
     * =====================================================
     */
    @Bean(
        initMethod = "start",
        destroyMethod = "shutdown"
    )
    @DependsOn({ "trustCertificateSynchronizationScheduler", "trustRootCertificateInitializer" })
    public Server grpcServer(

        GrpcServerCredentialsProvider credentialsProvider,

        LoginGrpcApi loginGrpcApi,

        RegistrationGrpcApi registrationGrpcApi

    ) throws Exception {

        System.out.println(">>> STARTING GRPC SERVER mTLS (port 9092) <<<");

        return NettyServerBuilder

            .forPort(
                9092,
                credentialsProvider.buildServerCredentials()
            )

            .addService(loginGrpcApi)

            .addService(registrationGrpcApi)

            .build();
    }
}
