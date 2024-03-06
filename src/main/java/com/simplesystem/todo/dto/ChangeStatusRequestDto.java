package com.simplesystem.todo.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.simplesystem.todo.entity.ItemStatus;
import jakarta.annotation.Nonnull;

@JsonClassDescription("This represents the structure of request for changing the status of the todo item")
public record ChangeStatusRequestDto(@JsonPropertyDescription("Status of the todo item. It could be anything from the allowed values (NOT_DONE, DONE, PAST_DUE). The default value is NOT_DONE") @Nonnull ItemStatus status) {
}
