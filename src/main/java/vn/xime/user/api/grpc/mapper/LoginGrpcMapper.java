package vn.xime.user.api.grpc.mapper;

import org.springframework.stereotype.Component;

import io.grpc.Status;

import vn.xime.user.domain.authentication.model.IdentifierType;

import vn.xime.user.domain.authentication.model.VerifiedIdentity;

import vn.xime.user.domain.credential.model.CredentialType;

import vn.xime.user.application.dto.external.authentication.VerifyCredentialRequest;

import vn.xime.user.application.dto.external.authentication.VerifyCredentialResponse;

import vn.xime.user.domain.sharedkernel.service.IdService;

import vn.xime.user.grpc.internal.authentication.VerifiedIdentityDto;



/**
 * =========================================================
 * LOGIN GRPC MAPPER
 * =========================================================
 *
 * Mapper:
 *
 * Proto
 * <->
 * Application DTO
 *
 * =========================================================
 */
@Component
public class LoginGrpcMapper {

    /**
     * =====================================================
     * PROTO -> APPLICATION REQUEST
     * =====================================================
     */
    public VerifyCredentialRequest
    toApplicationRequest(
        vn.xime.user.grpc.internal.authentication
            .VerifyCredentialRequest request
    ) {

        return new VerifyCredentialRequest(

            request.getIdentifier(),

            mapIdentifierType(
                request.getIdentifierType()
            ),

            request.getCredential(),

            mapCredentialType(
                request.getCredentialType()
            ),

            request.getUserAgent(),

            request.getShardId()
        );
    }


    /**
     * =====================================================
     * APPLICATION RESPONSE -> PROTO
     * =====================================================
     */
    public vn.xime.user.grpc.internal.authentication
        .VerifyCredentialResponse
    toProtoResponse(
        VerifyCredentialResponse response
    ) {

        var builder =
            vn.xime.user.grpc.internal.authentication
                .VerifyCredentialResponse
                .newBuilder()

                .setSuccess(
                    response.success()
                )

                .setFailureReason(
                    response.failureReason() == null
                        ? ""
                        : response.failureReason()
                )

                .setLocked(
                    response.locked()
                )

                .setDisabled(
                    response.disabled()
                )

                .setRequiresMfa(
                    response.requiresMfa()
                );


        // =============================================
        // VERIFIED IDENTITY
        // =============================================

        if (response.identity() != null) {

            builder.setIdentity(
                toProtoIdentity(
                    response.identity()
                )
            );
        }

        return builder.build();
    }


    /**
     * =====================================================
     * VERIFIED IDENTITY
     * =====================================================
     */
    private VerifiedIdentityDto
    toProtoIdentity(
        VerifiedIdentity identity
    ) {

        VerifiedIdentityDto.Builder builder =
            VerifiedIdentityDto.newBuilder()

                .setIdentityId(
                    IdService.toString(
                        identity.getIdentityId()
                    )
                )

                .setSubjectType(
                    identity.getSubjectType()
                )

                .setShardId(
                    identity.getShardId()
                )

                .setServiceId(
                    identity.getServiceId()
                );


        // =============================================
        // OPTIONAL TENANT
        // =============================================

        if (identity.getTenantId() != null) {

            builder.setTenantId(
                identity.getTenantId()
            );
        }

        return builder.build();
    }


    /**
     * =====================================================
     * IDENTIFIER TYPE
     * =====================================================
     */
    private IdentifierType mapIdentifierType(
        vn.xime.user.grpc.internal.authentication
            .IdentifierType type
    ) {

        return switch (type) {

            case USERNAME ->
                IdentifierType.USERNAME;

            case EMAIL ->
                IdentifierType.EMAIL;

            case PHONE ->
                IdentifierType.PHONE;

            default ->
                throw new IllegalArgumentException(
                    "unsupported identifier type"
                );
        };
    }


    /**
     * =====================================================
     * CREDENTIAL TYPE
     * =====================================================
     */
    private CredentialType mapCredentialType(
        vn.xime.user.grpc.internal.authentication
            .CredentialType type
    ) {

        return switch (type) {

            case PASSWORD ->
                CredentialType.PASSWORD;

            case PASSKEY ->
                CredentialType.PASSKEY;

            case OAUTH_GOOGLE ->
                CredentialType.OAUTH_GOOGLE;

            case OAUTH_GITHUB ->
                CredentialType.OAUTH_GITHUB;

            case API_KEY ->
                CredentialType.API_KEY;

            case CERTIFICATE ->
                CredentialType.CERTIFICATE;

            default ->
                throw new IllegalArgumentException(
                    "unsupported credential type"
                );
        };
    }


    // =====================================================
    // ERROR MAPPER
    // =====================================================

    public RuntimeException toStatus(
        Exception exception
    ) {

        if (exception
            instanceof IllegalArgumentException) {

            return Status.INVALID_ARGUMENT
                .withDescription(
                    exception.getMessage()
                )
                .asRuntimeException();
        }

        if (exception
            instanceof IllegalStateException) {

            return Status.FAILED_PRECONDITION
                .withDescription(
                    exception.getMessage()
                )
                .asRuntimeException();
        }

        return Status.INTERNAL
            .withDescription(
                exception.getMessage()
            )
            .asRuntimeException();
    }
}