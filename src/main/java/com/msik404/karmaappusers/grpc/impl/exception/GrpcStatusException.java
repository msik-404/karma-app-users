package com.msik404.karmaappusers.grpc.impl.exception;

import io.grpc.StatusRuntimeException;
import org.springframework.lang.NonNull;

public interface GrpcStatusException {

    @NonNull
    StatusRuntimeException asStatusRuntimeException();

}
