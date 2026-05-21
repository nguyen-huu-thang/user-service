package vn.xime.user.application.dto.external.identity;

import vn.xime.user.domain.credential.model.CredentialType;
import vn.xime.user.domain.authentication.model.IdentifierType;

/**
 * =========================================================
 * REGISTER USER REQUEST
 * =========================================================
 *
 * DTO dùng cho:
 *
 * Identity Service
 * ->
 * gRPC
 * ->
 * User Service
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * Chứa:
 *
 * - normalized identifier
 * - credential raw
 * - credential type
 * - device metadata
 * - target shard allocation
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * identifier:
 *
 * - đã được normalize bởi Identity Service
 *
 * shardId:
 *
 * - shard được allocate bởi routing layer
 *
 * credential:
 *
 * - raw credential
 * - KHÔNG được log
 *
 * =========================================================
 */
public record RegisterUserRequest(

    /**
     * Identifier đã normalize.
     *
     * Ví dụ:
     * - username
     * - email
     * - phone
     */
    String identifier,

    /**
     * Loại identifier.
     *
     * Ví dụ:
     * - username
     * - email
     * - phone
     */
    IdentifierType identifierType,

    /**
     * Raw credential.
     *
     * Ví dụ:
     * - password
     *
     * ⚠ KHÔNG được log.
     */
    String credential,

    /**
     * Credential type.
     */
    CredentialType credentialType,

    /**
     * User-Agent / device metadata.
     */
    String userAgent,

    /**
     * Target shard id.
     *
     * Được allocate bởi Identity Service.
     */
    String shardId

) {
}