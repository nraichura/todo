package com.example.todo.exception;

import java.util.List;

public record GenericErrorModel(List<GenericErrorModelBody> errors) {
    public record GenericErrorModelBody(List<String> body) {
    }
}
