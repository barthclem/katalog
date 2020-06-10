package com.klem.catalog.controllers.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {

    private boolean status;
    private String message;
    private T data;
}
