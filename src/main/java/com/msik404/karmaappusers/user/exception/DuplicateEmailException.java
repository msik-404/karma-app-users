package com.msik404.karmaappusers.user.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import com.msik404.karmaappusers.grpc.impl.exception.GrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class DuplicateEmailException extends RuntimeException implements EncodableException, GrpcStatusException {

    private static final String Id = "DuplicateEmail";
    private static final String ERROR_MESSAGE = "Document with provided email exists.";

    public DuplicateEmailException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(getExceptionId(), ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getExceptionId() {
        return Id;
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.ALREADY_EXISTS
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
