package com.example.todo.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.*;

@Value
@JsonClassDescription("This represents the structure of request while creating the new todo item")
public class CreateItemRequestDto {
  @Nonnull
  @NotBlank(message = "Description cannot be blank")
  @JsonPropertyDescription("Description of the todo item")
  String description;

  @Nonnull
  @JsonPropertyDescription(
      "Due date of the todo item representing the date after which the item will move to PAST_DUE state")
  Instant dueAt;
}
