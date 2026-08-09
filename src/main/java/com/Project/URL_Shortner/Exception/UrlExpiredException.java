package com.Project.URL_Shortner.Exception;

public class UrlExpiredException extends RuntimeException {
    public UrlExpiredException(String message) {
        super(message);
    }
}
