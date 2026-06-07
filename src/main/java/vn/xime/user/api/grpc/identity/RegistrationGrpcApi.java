package vn.xime.user.api.grpc.identity;

import lombok.RequiredArgsConstructor;

import io.grpc.stub.StreamObserver;

import org.springframework.stereotype.Service;

import vn.xime.user.api.grpc.mapper.RegistrationGrpcMapper;
import vn.xime.user.application.usecase.identity.RegisterUseCase;
import vn.xime.user.grpc.internal.authentication.RegisterUserRequest;

import vn.xime.user.grpc.internal.authentication.RegisterUserResponse;

import vn.xime.user.grpc.internal.authentication.RegistrationServiceGrpc;



/**
 * =========================================================
 * REGISTRATION GRPC API
 * =========================================================
 *
 * Internal gRPC API:
 *
 * Identity Service
 * ->
 * User Service
 *
 * =========================================================
 * RESPONSIBILITY
 * =========================================================
 *
 * - receive grpc request
 * - execute usecase
 * - return grpc response
 *
 * =========================================================
 */
@Service
@RequiredArgsConstructor
public class RegistrationGrpcApi extends
    RegistrationServiceGrpc
        .RegistrationServiceImplBase {

    /**
     * =====================================================
     * REGISTER USE CASE
     * =====================================================
     */
    private final RegisterUseCase registerUseCase;


    /**
     * =====================================================
     * GRPC MAPPER
     * =====================================================
     */
    private final RegistrationGrpcMapper registrationGrpcMapper;


    /**
     * =====================================================
     * REGISTER USER
     * =====================================================
     */
    @Override
    public void registerUser(
        RegisterUserRequest request,
        StreamObserver<RegisterUserResponse>
            responseObserver
    ) {

        try {

            // =============================================
            // PROTO -> APPLICATION DTO
            // =============================================

            var applicationRequest =
                registrationGrpcMapper
                    .toApplicationRequest(
                        request
                    );


            // =============================================
            // EXECUTE USE CASE
            // =============================================

            var applicationResponse =
                registerUseCase.execute(
                    applicationRequest
                );


            // =============================================
            // APPLICATION DTO -> PROTO
            // =============================================

            RegisterUserResponse response =
                registrationGrpcMapper
                    .toProtoResponse(
                        applicationResponse
                    );


            // =============================================
            // SUCCESS
            // =============================================

            responseObserver.onNext(
                response
            );

            responseObserver.onCompleted();

        } catch (Exception exception) {

            responseObserver.onError(
                registrationGrpcMapper
                    .toStatus(exception)
            );
        }
    }
}