package vn.xime.user.api.grpc.identity.mapper;

import org.springframework.stereotype.Component;

import io.grpc.Status;

import vn.xime.user.domain.authentication.model
    .IdentifierType;

import vn.xime.user.domain.credential.model
    .CredentialType;

import vn.xime.user.application.dto.external.identity
    .RegisterUserRequest;

import vn.xime.user.application.dto.external.identity
    .RegisterUserResponse;



/**
 * =========================================================
 * REGISTRATION GRPC MAPPER
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
public class RegistrationGrpcMapper {

    /**
     * =====================================================
     * PROTO -> APPLICATION REQUEST
     * =====================================================
     */
    public RegisterUserRequest
    toApplicationRequest(
        vn.xime.user.grpc.internal.authentication
            .RegisterUserRequest request
    ) {

        return new RegisterUserRequest(

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
        .RegisterUserResponse
    toProtoResponse(
        RegisterUserResponse response
    ) {

        return
            vn.xime.user.grpc.internal.authentication
                .RegisterUserResponse
                .newBuilder()

                .setIdentityId(
                    response.identityId()
                )

                .setShardId(
                    response.shardId()
                )

                .setCreatedAt(
                    response.createdAt()
                        .toEpochMilli()
                )

                .build();
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