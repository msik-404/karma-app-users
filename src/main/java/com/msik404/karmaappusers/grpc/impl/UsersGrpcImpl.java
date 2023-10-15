package com.msik404.karmaappusers.grpc.impl;

import java.util.List;

import build.buf.protovalidate.ValidationResult;
import build.buf.protovalidate.Validator;
import build.buf.protovalidate.exceptions.ValidationException;
import com.google.protobuf.Empty;
import com.google.protobuf.Message;
import com.msik404.karmaappusers.grpc.*;
import com.msik404.karmaappusers.grpc.impl.exception.UnsupportedRoleException;
import com.msik404.karmaappusers.grpc.impl.mapper.GrpcToDocMapper;
import com.msik404.karmaappusers.grpc.impl.mapper.RoleMapper;
import com.msik404.karmaappusers.user.Role;
import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.UserService;
import com.msik404.karmaappusers.user.dto.IdAndHashedPasswordAndRoleOnlyDto;
import com.msik404.karmaappusers.user.dto.UserUpdateDto;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserDocumentNotFoundException;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersGrpcImpl extends UsersGrpc.UsersImplBase {

    private final UserService service;

    private static <T> boolean validate(
            @NonNull final Message request,
            @NonNull final StreamObserver<T> responseObserver) {

        final Validator validator = new Validator();
        try {
            final ValidationResult result = validator.validate(request);
            // Check if there are any validation violations
            if (!result.isSuccess()) {
                responseObserver.onError(Status.INVALID_ARGUMENT
                        .withDescription(result.toString())
                        .asRuntimeException()
                );
                return false;
            }
        } catch (ValidationException ex) {
            // Catch and print any ValidationExceptions thrown during the validation process
            final String errMessage = ex.getMessage();
            System.out.println("Validation failed: " + errMessage);
            responseObserver.onError(Status.INTERNAL
                    .withDescription(errMessage)
                    .asRuntimeException()
            );
            return false;
        }
        return true;
    }

    @Override
    public void createUser(
            @NonNull final CreateUserRequest request,
            @NonNull final StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final UserDocument doc = GrpcToDocMapper.map(request);
            service.save(doc);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (UnsupportedRoleException | DuplicateUsernameException | DuplicateEmailException |
                 DuplicateUnexpectedFieldException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void updateUser(
            @NonNull final UpdateUserRequest request,
            @NonNull final StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final UserUpdateDto dto = GrpcToDocMapper.map(request);
            service.update(dto);

            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();

        } catch (UnsupportedRoleException | DuplicateUsernameException | DuplicateEmailException |
                 DuplicateUnexpectedFieldException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void findCredentials(
            @NonNull final CredentialsRequest request,
            @NonNull final StreamObserver<CredentialsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final IdAndHashedPasswordAndRoleOnlyDto credentials = service.findCredentials(request.getEmail());

            final var response = CredentialsResponse.newBuilder()
                    .setUserId(MongoObjectId.newBuilder().setHexString(credentials.id().toHexString()).build())
                    .setPassword(credentials.hashedPassword())
                    .setRole(RoleMapper.map(credentials.role()))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserDocumentNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        } catch (UnsupportedRoleException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }

        super.findCredentials(request, responseObserver);
    }

    @Override
    public void findUserRole(
            @NonNull final UserRoleRequest request,
            @NonNull final StreamObserver<UserRoleResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final Role role = service.findRole(new ObjectId(request.getUserId().getHexString()));

            final var response = UserRoleResponse.newBuilder()
                    .setRole(RoleMapper.map(role))
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserDocumentNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }

        super.findUserRole(request, responseObserver);
    }

    @Override
    public void findUsername(
            @NonNull final UsernameRequest request,
            @NonNull final StreamObserver<UsernameResponse> responseObserver) {

        try {
            final String username = service.findUsername(new ObjectId(request.getUserId().getHexString()));

            final var response = UsernameResponse.newBuilder().setUsername(username).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserDocumentNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }
    }

    @Override
    public void findUsernames(
            @NonNull final UsernamesRequest request,
            @NonNull final StreamObserver<UsernamesResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        final List<ObjectId> userIds = request.getUserIdsList().stream()
                .map(grpcId -> new ObjectId(grpcId.getHexString()))
                .toList();

        final List<String> usernames = service.findUsernames(userIds);

        final var response = UsernamesResponse.newBuilder()
                .addAllUsernames(usernames).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void findUserId(
            @NonNull final UserIdRequest request,
            @NonNull final StreamObserver<MongoObjectId> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final ObjectId userId = service.findUserId(request.getUsername());

            final var response = MongoObjectId.newBuilder().setHexString(userId.toHexString()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserDocumentNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }
    }

}
