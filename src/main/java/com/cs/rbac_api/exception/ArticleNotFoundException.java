package com.cs.rbac_api.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(String message) { super(message); }
}
