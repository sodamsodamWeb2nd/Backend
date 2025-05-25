package com.example.sodamsodam.apps.common.exception;

public class DuplicatePhoneNumberException extends RuntimeException {
    public DuplicatePhoneNumberException(String message) {
        super(message);
    }
}