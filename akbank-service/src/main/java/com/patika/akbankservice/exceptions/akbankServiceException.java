package com.patika.akbankservice.exceptions;

import lombok.Getter;

@Getter
public class akbankServiceException extends RuntimeException {

    private String errorMessage;

    public akbankServiceException(String message) {
        super(message);
        this.errorMessage = message;
    }
}
