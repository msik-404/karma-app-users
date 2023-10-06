package com.msik404.karmaappusers.grpc.impl.exception;

public class UnsupportedRoleException extends RuntimeException {

    public UnsupportedRoleException() {
        super("Unsupported role provided.");
    }

}
