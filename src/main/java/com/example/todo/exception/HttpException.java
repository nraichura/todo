package com.example.todo.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class HttpException extends RuntimeException {

  private final HttpStatus status;
  private final String description;
}
