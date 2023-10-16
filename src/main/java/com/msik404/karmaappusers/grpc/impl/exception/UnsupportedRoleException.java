package com.msik404.karmaappusers.grpc.impl.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import org.springframework.lang.NonNull;

public class UnsupportedRoleException extends RuntimeException implements EncodableException {

    private static final String ERROR_MESSAGE = "Unsupported role provided.";

    public UnsupportedRoleException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(UnsupportedRoleException.class.getSimpleName(), ERROR_MESSAGE);
    }

}
