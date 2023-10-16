package com.msik404.karmaappusers.user.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import org.springframework.lang.NonNull;

public class DuplicateEmailException extends RuntimeException implements EncodableException {

    private static final String ERROR_MESSAGE = "Document with provided email exists.";

    public DuplicateEmailException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(DuplicateEmailException.class.getSimpleName(), ERROR_MESSAGE);
    }

}
