package com.app.exceptions;

public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomException() {
        super();
    }

    public CustomException(Exception e) {
        super(e);
    }

    public CustomException(String message) {
        super(message);
    }
}
