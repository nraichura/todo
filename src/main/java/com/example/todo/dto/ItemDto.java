package com.example.todo.dto;

import com.example.todo.entity.ItemStatus;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.Instant;
import lombok.*;

@Value
@JsonClassDescription("This represents the structure of todo item")
public class ItemDto {
  @Nonnull
  @JsonPropertyDescription("Id of the todo item")
  Long id;

  @Nonnull
  @JsonPropertyDescription("Description of the todo item")
  String description;

  @Nonnull
  @JsonPropertyDescription(
      "Status of the todo item. It could be anything from the allowed values (NOT_DONE, DONE, PAST_DUE). The default value is NOT_DONE")
  ItemStatus status;

  @Nonnull
  @JsonPropertyDescription("Creation date of the todo item")
  Instant createdAt;

  @Nonnull
  @JsonPropertyDescription(
      "Due date of the todo item representing the date after which the item will move to PAST_DUE state")
  Instant dueAt;

  @Nullable
  @JsonPropertyDescription("The date at which the todo item was moved to DONE state")
  Instant markedDoneAt;
}
