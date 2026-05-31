package vn.xime.user.infrastructure.grpc.channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Component;

import io.grpc.ManagedChannel;

import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;

import lombok.RequiredArgsConstructor;

import vn.xime.user.integration.trust.ssl.TrustSslContextProvider;


/**
 * =========================================================
 * gRPC CHANNEL PROVIDER
 * =========================================================
 *
 * Shared gRPC channel manager.
 *
 * Responsibilities:
 *
 * - create channel
 * - cache channel
 * - reuse grpc connection
 * - manage channel lifecycle
 *
 * KHÔNG:
 *
 * - service discovery
 * - routing logic
 * - grpc request handling
 * - protobuf mapping
 *
 * =========================================================
 */
@Component
@RequiredArgsConstructor
public class GrpcChannelProvider {

    /**
     * =====================================================
     * TRUST SSL CONTEXT PROVIDER
     * =====================================================
     */
    private final TrustSslContextProvider trustSslContextProvider;


    /**
     * =====================================================
     * CHANNEL CACHE
     * =====================================================
     *
     * Key:
     *
     * host:port
     *
     * =====================================================
     */
    private final Map<String, ManagedChannel>
        channels = new ConcurrentHashMap<>();


    /**
     * =====================================================
     * GET CHANNEL
     * =====================================================
     */
    public ManagedChannel getChannel(
        String host,
        int port
    ) {

        String key =
            host
                + ":"
                + port;

        return channels.computeIfAbsent(

            key,

            ignored ->

                NettyChannelBuilder

                    .forAddress(
                        host,
                        port
                    )

                    .sslContext(
                        trustSslContextProvider
                            .getSslContext()
                    )

                    .build()
        );
    }


    /**
     * =====================================================
     * CLEAR CHANNELS
     * =====================================================
     *
     * Future:
     *
     * certificate rotation
     * →
     * rebuild channels
     *
     * =====================================================
     */
    public synchronized void clearChannels() {

        channels
            .values()
            .forEach(
                this::shutdownChannel
            );

        channels.clear();
    }


    /**
     * =====================================================
     * SHUTDOWN
     * =====================================================
     */
    @PreDestroy
    public void shutdown() {

        clearChannels();
    }


    /**
     * =====================================================
     * SHUTDOWN CHANNEL
     * =====================================================
     */
    private void shutdownChannel(
        ManagedChannel channel
    ) {

        try {

            channel.shutdown();

            channel.awaitTermination(
                5,
                TimeUnit.SECONDS
            );

        } catch (InterruptedException exception) {

            channel.shutdownNow();

            Thread.currentThread()
                .interrupt();
        }
    }
}