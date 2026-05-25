package vn.xime.user.application.port.out.integration;

import java.util.Optional;

import vn.xime.user.domain.sharedkernel.model.Id;
import vn.xime.user.domain.authentication.model.KeyContext;

/**
 * Outbound port dùng để resolve verification key.
 *
 * Application layer chỉ biết:
 *
 * - cần verification key để verify JWT
 * - key đến từ external trust infrastructure
 *
 * Application KHÔNG biết:
 *
 * - grpc
 * - rest
 * - redis
 * - cache
 * - trust service implementation
 * - key storage implementation
 */
public interface ResolveVerificationKey {

    /**
     * Resolve public verification key theo key id (kid).
     */
    Optional<KeyContext> resolve(
        Id keyId
    );
}