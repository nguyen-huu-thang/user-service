package vn.xime.user.application.dto.external.authentication;

import vn.xime.user.domain.authentication.model.IdentifierType;
import vn.xime.user.domain.credential.model.CredentialType;


/**
 * =========================================================
 * VERIFY CREDENTIAL REQUEST
 * =========================================================
 *
 * DTO dùng cho:
 *
 * Identity Service
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
 * - target shard
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * identifier:
 *
 * - đã normalize từ Identity Service
 *
 * credential:
 *
 * - raw credential
 * - KHÔNG được log
 *
 * shardId:
 *
 * - shard target đã resolve
 * - dùng cho shard-aware routing
 *
 * =========================================================
 */
public record VerifyCredentialRequest(

    /**
     * Identifier đã normalize.
     *
     * Ví dụ:
     *
     * - username
     * - email
     * - phone
     */
    String identifier,


    /**
     * Loại identifier.
     *
     * Ví dụ:
     *
     * - USERNAME
     * - EMAIL
     * - PHONE
     */
    IdentifierType identifierType,


    /**
     * Raw credential.
     *
     * Ví dụ:
     *
     * - password
     * - oauth token
     * - signed challenge
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
     *
     * Dùng cho:
     *
     * - anomaly detection
     * - audit
     * - trusted device analysis
     */
    String userAgent,


    /**
     * Target user shard.
     *
     * Được resolve bởi Identity Service.
     */
    String shardId

) {
}