package com.msik404.karmaappusers.grpc.impl;

import java.util.List;
import java.util.Optional;

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
import com.msik404.karmaappusers.user.dto.IdAndHashedPasswordOnlyDto;
import com.msik404.karmaappusers.user.dto.UserDto;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserDocumentNotFoundException;
import com.msik404.karmaappusers.user.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsersGrpcImpl extends UsersGrpc.UsersImplBase {

    private final UserService service;
    private final UserRepository repository;

    private static <T> boolean validate(Message request, StreamObserver<T> responseObserver) {

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
    public void createUser(CreateUserRequest request, StreamObserver<Empty> responseObserver) {

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
    public void updateUser(UpdateUserRequest request, StreamObserver<Empty> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final UserDto dto = GrpcToDocMapper.map(request);
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
    public void findCredentials(CredentialsRequest request, StreamObserver<CredentialsResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        try {
            final IdAndHashedPasswordOnlyDto credentials = service.findCredentials(request.getEmail());

            final var response = CredentialsResponse.newBuilder()
                    .setUserId(MongoObjectId.newBuilder().setHexString(credentials.id().toString()).build())
                    .setPassword(credentials.password())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (UserDocumentNotFoundException ex) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(ex.getMessage())
                    .asRuntimeException()
            );
        }

        super.findCredentials(request, responseObserver);
    }

    @Override
    public void findUserRole(UserRoleRequest request, StreamObserver<UserRoleResponse> responseObserver) {

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
    public void findUsernames(UsernamesRequest request, StreamObserver<UsernamesResponse> responseObserver) {

        final boolean isSuccess = validate(request, responseObserver);
        if (!isSuccess) {
            return;
        }

        final List<ObjectId> userIds = request.getUserIdsList().stream()
                .map(grpcId -> new ObjectId(grpcId.getHexString()))
                .toList();

        final List<Optional<String>> usernames = repository.findUsernames(userIds);

        final var response = UsernamesResponse.newBuilder()
                .addAllUsernames(
                        usernames.stream().map(optional -> optional.orElse("")).toList()
                ).build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
