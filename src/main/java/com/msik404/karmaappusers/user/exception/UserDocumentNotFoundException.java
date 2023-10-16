package com.msik404.karmaappusers.user.exception;

import com.msik404.karmaappusers.encoding.EncodableException;
import com.msik404.karmaappusers.encoding.ExceptionEncoder;
import org.springframework.lang.NonNull;

public class UserDocumentNotFoundException extends RuntimeException implements EncodableException {

    private static final String ERROR_MESSAGE = "Requested UserDocument was not found.";

    public UserDocumentNotFoundException() {
        super(ERROR_MESSAGE);
    }

    @NonNull
    @Override
    public String getEncodedException() {
        return ExceptionEncoder.encode(UserDocumentNotFoundException.class.getSimpleName(), ERROR_MESSAGE);
    }

}
