package com.msik404.karmaappusers.encoding;

import org.springframework.lang.NonNull;

public class ExceptionEncoder {

    public final static String EXCEPTION_ID_PREFIX = "exceptionId: ";

    @NonNull
    public static String encode(@NonNull String exceptionIdString, @NonNull String errorMessage) {
        return String.format("%s%s %s", EXCEPTION_ID_PREFIX, exceptionIdString, errorMessage);
    }

}