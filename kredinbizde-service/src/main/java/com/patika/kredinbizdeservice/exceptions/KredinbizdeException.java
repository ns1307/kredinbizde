package com.patika.kredinbizdeservice.exceptions;

public class KredinbizdeException extends RuntimeException {

    private String errorMessage;

    public KredinbizdeException(String message) {
        super(message);
        this.errorMessage = message;
    }
}
