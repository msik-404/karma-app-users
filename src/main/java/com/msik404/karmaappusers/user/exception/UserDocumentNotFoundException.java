package com.msik404.karmaappusers.user.exception;

public class UserDocumentNotFoundException extends RuntimeException {

    public UserDocumentNotFoundException() {
        super("Requested UserDocument was not found.");
    }

}
