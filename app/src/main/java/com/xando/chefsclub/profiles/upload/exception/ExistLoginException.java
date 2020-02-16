package com.xando.chefsclub.profiles.upload.exception;


public class ExistLoginException extends Exception {

    public ExistLoginException() {
        super("Login already exist");
    }

    public ExistLoginException(String message) {
        super(message);
    }
}
