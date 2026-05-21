package vn.xime.user.application.dto.external.authentication;

import vn.xime.user.domain.authentication.model.VerifiedIdentity;


/**
 * =========================================================
 * VERIFY CREDENTIAL RESPONSE
 * =========================================================
 *
 * DTO response cho credential verification flow.
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * Trả về:
 *
 * - verification result
 * - verified identity
 * - account state
 * - failure metadata
 *
 * =========================================================
 * NOTE
 * =========================================================
 *
 * identity:
 *
 * - chỉ tồn tại khi success = true
 *
 * =========================================================
 */
public record VerifyCredentialResponse(

    /**
     * Credential verification success.
     */
    boolean success,


    /**
     * Verified identity.
     *
     * Chỉ tồn tại khi success = true.
     */
    VerifiedIdentity identity,


    /**
     * Failure reason.
     *
     * Ví dụ:
     *
     * - INVALID_CREDENTIAL
     * - ACCOUNT_LOCKED
     * - ACCOUNT_DISABLED
     * - MFA_REQUIRED
     */
    String failureReason,


    /**
     * Account locked state.
     */
    boolean locked,


    /**
     * Account disabled state.
     */
    boolean disabled,


    /**
     * MFA required before authentication success.
     */
    boolean requiresMfa

) {
}