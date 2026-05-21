package vn.xime.user.domain.authentication.model;

import vn.xime.user.domain.sharedkernel.model.Id;

public class VerifiedIdentity {

    /**
     * Identity ID trong authentication platform.
     *
     * Đây là authenticated subject ID.
     *
     * Cũng chính là subject ID thực tế
     * ở domain owner service.
     */
    private final Id identityId;

    /**
     * Loại subject đã authenticate.
     *
     * Ví dụ:
     * - HUMAN
     * - BOT
     * - SERVICE
     * - SYSTEM
     */
    private final String subjectType;

    /**
     * Shard chứa subject thực tế.
     */
    private final String shardId;

    /**
     * Service sở hữu credential/domain.
     *
     * Ví dụ:
     * - user-service
     * - bot-service
     * - system-service
     */
    private final String serviceId;

    /**
     * Tenant của identity.
     */
    private final String tenantId;

    public VerifiedIdentity(
            Id identityId,
            String subjectType,
            String shardId,
            String serviceId,
            String tenantId
    ) {

        if (identityId == null) {
            throw new IllegalArgumentException(
                    "identityId is null"
            );
        }

        if (subjectType == null || subjectType.isBlank()) {
            throw new IllegalArgumentException(
                    "subjectType is invalid"
            );
        }

        if (shardId == null || shardId.isBlank()) {
            throw new IllegalArgumentException(
                    "shardId is invalid"
            );
        }

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "serviceId is invalid"
            );
        }

        this.identityId = identityId;
        this.subjectType = subjectType;
        this.shardId = shardId;
        this.serviceId = serviceId;
        this.tenantId = tenantId;
    }

    public Id getIdentityId() {
        return identityId;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getShardId() {
        return shardId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getTenantId() {
        return tenantId;
    }
}