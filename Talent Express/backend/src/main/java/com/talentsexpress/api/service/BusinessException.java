package com.talentsexpress.api.service;

/**
 * Exceção de regra de negócio — traduzida para HTTP 400 pelo GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
