package vn.xime.user.api.grpc.identity;

import lombok.RequiredArgsConstructor;

import io.grpc.stub.StreamObserver;

import org.springframework.grpc.server.service.GrpcService;

import vn.xime.user.api.grpc.mapper.LoginGrpcMapper;
import vn.xime.user.application.usecase.identity.LoginUseCase;
import vn.xime.user.grpc.internal.authentication.LoginServiceGrpc;

import vn.xime.user.grpc.internal.authentication.VerifyCredentialRequest;

import vn.xime.user.grpc.internal.authentication.VerifyCredentialResponse;



/**
 * =========================================================
 * LOGIN GRPC API
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
 * SECURITY
 * =========================================================
 *
 * - internal only
 * - mTLS protected
 *
 * =========================================================
 */
@GrpcService
@RequiredArgsConstructor
public class LoginGrpcApi extends LoginServiceGrpc.LoginServiceImplBase {

    /**
     * =====================================================
     * LOGIN USE CASE
     * =====================================================
     */
    private final LoginUseCase loginUseCase;


    /**
     * =====================================================
     * GRPC MAPPER
     * =====================================================
     */
    private final LoginGrpcMapper loginGrpcMapper;


    /**
     * =====================================================
     * VERIFY CREDENTIAL
     * =====================================================
     */
    @Override
    public void verifyCredential(
        VerifyCredentialRequest request,
        StreamObserver<VerifyCredentialResponse>
            responseObserver
    ) {

        try {

            // =============================================
            // PROTO -> APPLICATION DTO
            // =============================================

            var applicationRequest =
                loginGrpcMapper
                    .toApplicationRequest(
                        request
                    );


            // =============================================
            // EXECUTE USE CASE
            // =============================================

            var applicationResponse =
                loginUseCase.execute(
                    applicationRequest
                );


            // =============================================
            // APPLICATION DTO -> PROTO
            // =============================================

            VerifyCredentialResponse response =
                loginGrpcMapper
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
                loginGrpcMapper.toStatus(
                    exception
                )
            );
        }
    }
}