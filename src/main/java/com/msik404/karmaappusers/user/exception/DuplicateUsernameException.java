package com.msik404.karmaappusers.user.exception;

public class DuplicateUsernameException extends RuntimeException {

    public DuplicateUsernameException() {
        super("Document with provided username exists.");
    }

}
