package io.springbatch.springbatchlecture.exception;

public class RetryableException extends Exception {

    public RetryableException() {
        super();
    }

    public RetryableException(String message) {
        super(message);
    }

}
