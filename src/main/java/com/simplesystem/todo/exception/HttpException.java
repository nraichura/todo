package com.simplesystem.todo.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class HttpException extends RuntimeException {

    private final HttpStatus status;
    private final String description;

}
