package com.Project.URL_Shortner.Exception;

public class UrlNotFoundException extends RuntimeException{
    public UrlNotFoundException(String message){
        super(message);
    }
}
