package com.msik404.karmaappusers.user.exception;

public class DuplicateUnexpectedFieldException extends RuntimeException {

    public DuplicateUnexpectedFieldException() {
        super("Document with provided value exists. Specific field is unknown.");
    }

}
