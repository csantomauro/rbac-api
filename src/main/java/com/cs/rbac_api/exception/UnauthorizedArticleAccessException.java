package com.cs.rbac_api.exception;

public class UnauthorizedArticleAccessException extends RuntimeException {
    public UnauthorizedArticleAccessException(String message) { super(message); }
}
