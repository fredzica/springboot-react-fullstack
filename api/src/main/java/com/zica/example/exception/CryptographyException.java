package com.zica.example.exception;

public class CryptographyException extends RuntimeException {
    public CryptographyException(Exception e) {
        super(e);
    }
}
