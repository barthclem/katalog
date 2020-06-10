package com.klem.catalog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceConflictException extends  RuntimeException{

    public ResourceConflictException(String s) {
        super(s);
    }

    public ResourceConflictException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
