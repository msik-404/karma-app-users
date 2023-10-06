package com.msik404.karmaappusers.user.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException() {
        super("Document with provided email exists.");
    }

}
