package vn.xime.user.application.dto.external.identity;

import java.time.Instant;



/**
 * =========================================================
 * REGISTER USER RESPONSE
 * =========================================================
 *
 * Kết quả trả về cho Identity Service
 * sau khi User Service tạo user thành công.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * Chứa:
 *
 * - identity id
 * - shard location
 * - creation timestamp
 *
 * =========================================================
 */
public record RegisterUserResponse(

    /**
     * Identity ID.
     *
     * Cũng chính là user ID.
     */
    String identityId,

    /**
     * User shard id.
     */
    String shardId,

    /**
     * User creation timestamp.
     */
    Instant createdAt

) {
}