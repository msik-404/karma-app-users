package com.msik404.karmaappusers.user.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import org.springframework.lang.NonNull;

public class DuplicateUsernameException extends RuntimeException implements EncodableException {

    private static final String ERROR_MESSAGE = "Document with provided username exists.";

    public DuplicateUsernameException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(DuplicateUsernameException.class.getSimpleName(), ERROR_MESSAGE);
    }

}
