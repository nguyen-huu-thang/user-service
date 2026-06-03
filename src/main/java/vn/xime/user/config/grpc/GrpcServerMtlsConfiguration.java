package vn.xime.user.config.grpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;

import org.springframework.grpc.server.GrpcServerConfigurer;

import vn.xime.user.integration.trust.ssl.GrpcServerSslContextProvider;


/**
 * =========================================================
 * GRPC SERVER mTLS CONFIGURATION
 * =========================================================
 *
 * Responsibilities:
 *
 * - apply server SSL context vào gRPC server khi khởi động
 * - wire GrpcServerSslContextProvider vào Spring gRPC lifecycle
 *
 * KHÔNG:
 *
 * - quản lý certificate
 * - quản lý channel
 * - handle gRPC request
 *
 * =========================================================
 * LIFECYCLE
 * =========================================================
 *
 * Spring gRPC gọi GrpcServerConfigurer.configure() một lần
 * khi build server, trước khi server bắt đầu nhận request.
 * SSL context được build tại thời điểm đó — cert phải đã
 * sẵn sàng trong TrustCertificateResolver (bootstrap xong).
 *
 * =========================================================
 */
@Configuration
public class GrpcServerMtlsConfiguration {

    /**
     * =====================================================
     * GRPC SERVER mTLS CONFIGURER
     * =====================================================
     *
     * Spring gRPC tự pick up tất cả GrpcServerConfigurer
     * beans và apply vào NettyServerBuilder khi khởi động.
     *
     * =====================================================
     */
    @Bean
    GrpcServerConfigurer grpcServerMtlsConfigurer(
        GrpcServerSslContextProvider serverSslContextProvider
    ) {

        return (ServerBuilder<?> serverBuilder) -> {

            if (serverBuilder instanceof NettyServerBuilder netty) {

                netty.sslContext(
                    serverSslContextProvider
                        .getServerSslContext()
                );
            }
        };
    }
}
