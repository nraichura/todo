package com.simplesystem.todo.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

import static com.simplesystem.todo.exception.GenericErrorModel.GenericErrorModelBody;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericErrorModel> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        return ResponseEntity.badRequest()
                .body(createErrorResponse(errors));
    }

    @ExceptionHandler(HttpException.class)
    public ResponseEntity<GenericErrorModel> handleBusinessException(HttpException e) {
        return ResponseEntity.status(e.getStatus())
                .body(createErrorResponse(List.of(e.getDescription())));
    }

    private GenericErrorModel createErrorResponse(List<String> e) {
        return new GenericErrorModel(List.of(new GenericErrorModelBody(e)));
    }
}

