package com.msik404.karmaappusers.user.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import com.msik404.karmaappusers.grpc.impl.exception.GrpcStatusException;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public class DuplicateUnexpectedFieldException extends RuntimeException implements EncodableException, GrpcStatusException {

    private static final String ERROR_MESSAGE = "Document with provided field value exists. Specific field is unknown.";

    public DuplicateUnexpectedFieldException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(DuplicateUnexpectedFieldException.class.getSimpleName(), ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public StatusRuntimeException asStatusRuntimeException() {
        return Status.ALREADY_EXISTS
                .withDescription(getEncodedException())
                .asRuntimeException();
    }

}
