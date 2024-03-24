package com.example.todo.exception;

import static com.example.todo.exception.GenericErrorModel.GenericErrorModelBody;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<GenericErrorModel> handleValidationException(
      MethodArgumentNotValidException e) {
    List<String> errors =
        e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .toList();
    return ResponseEntity.badRequest().body(createErrorResponse(errors));
  }

  @ExceptionHandler(HttpException.class)
  public ResponseEntity<GenericErrorModel> handleBusinessException(HttpException e) {
    return ResponseEntity.status(e.getStatus())
        .body(createErrorResponse(List.of(e.getDescription())));
  }

  @ExceptionHandler(Throwable.class)
  public ResponseEntity<GenericErrorModel> handleUnknownErrors(Throwable e) {
    log.error("Unknown error occurred", e);
    return ResponseEntity.internalServerError().body(createErrorResponse(List.of(e.getMessage())));
  }

  private GenericErrorModel createErrorResponse(List<String> e) {
    return new GenericErrorModel(List.of(new GenericErrorModelBody(e)));
  }
}
