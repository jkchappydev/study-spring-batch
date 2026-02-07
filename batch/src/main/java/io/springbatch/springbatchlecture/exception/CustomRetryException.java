package io.springbatch.springbatchlecture.exception;

public class CustomRetryException extends Exception {

    public CustomRetryException(String message) {
        super(message);
    }

}
