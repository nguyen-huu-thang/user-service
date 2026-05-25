package vn.xime.user.domain.authentication.model;

import vn.xime.user.domain.sharedkernel.model.Id;

import java.time.Instant;
import java.util.Objects;

public class JwtClaims {

    /**
     * JWT ID
     */
    private final Id tokenId;

    /**
     * Subject identity (user id)
     */
    private final Id identityId;

    /**
     * id dịch vụ phát hành token (id dịch vụ identity)
     */
    private final String issuer;

    /**
     * dịch vụ đích verify token.
     */
    private final String audience;

    /**
     * thời gian phát hành.
     */
    private final Instant issuedAt;

    /**
     * thời gian hết hạn.
     */
    private final Instant expiresAt;

    /**
     * thời gian bắt đầu có hiệu lực.
     */
    private final Instant notBefore;

    /**
     * thời gian người dùng đăng nhập.
     */
    private final Instant authenticatedAt;

    /**
     * phiên bản token.
     */
    private final Integer tokenVersion;


    public JwtClaims(
            Id tokenId,
            Id identityId,
            String issuer,
            String audience,
            Instant issuedAt,
            Instant expiresAt,
            Instant notBefore,
            Instant authenticatedAt,
            Integer tokenVersion
    ) {

        if (tokenVersion < 0) {
            throw new IllegalArgumentException(
                    "tokenVersion cannot be negative"
            );
        }

        if (expiresAt.isBefore(issuedAt)) {
            throw new IllegalArgumentException(
                    "expiresAt must be after issuedAt"
            );
        }

        this.tokenId = Objects.requireNonNull(tokenId);

        this.identityId = Objects.requireNonNull(identityId);

        this.issuer = Objects.requireNonNull(issuer);

        this.audience = Objects.requireNonNull(audience);

        this.issuedAt = Objects.requireNonNull(issuedAt);

        this.expiresAt = Objects.requireNonNull(expiresAt);

        this.notBefore = Objects.requireNonNull(notBefore);

        this.authenticatedAt = Objects.requireNonNull(authenticatedAt);

        this.tokenVersion = tokenVersion;
    }

    public boolean isExpiredAt(Instant now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isActiveAt(Instant now) {

        return !now.isBefore(notBefore)
                && !isExpiredAt(now);
    }

    public Id getTokenId() {
        return tokenId;
    }

    public Id getIdentityId() {
        return identityId;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getAudience() {
        return audience;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getNotBefore() {
        return notBefore;
    }

    public Instant getAuthenticatedAt() {
        return authenticatedAt;
    }

    public Integer getTokenVersion() {
        return tokenVersion;
    }
}