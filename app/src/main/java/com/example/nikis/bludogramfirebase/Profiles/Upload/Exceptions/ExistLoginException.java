package com.example.nikis.bludogramfirebase.Profiles.Upload.Exceptions;


public class ExistLoginException extends Exception {
    public ExistLoginException() {
        super("Login already exist");
    }

    public ExistLoginException(String message) {
        super(message);
    }
}
