package com.simplesystem.todo.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.simplesystem.todo.entity.ItemStatus;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.time.Instant;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonClassDescription("This represents the structure of todo item")
public class ItemDto {
  @Nonnull
  @JsonPropertyDescription("Id of the todo item")
  private Long id;

  @Nonnull
  @JsonPropertyDescription("Description of the todo item")
  private String description;

  @Nonnull
  @JsonPropertyDescription(
      "Status of the todo item. It could be anything from the allowed values (NOT_DONE, DONE, PAST_DUE). The default value is NOT_DONE")
  private ItemStatus status;

  @Nonnull
  @JsonPropertyDescription("Creation date of the todo item")
  private Instant createdAt;

  @Nonnull
  @JsonPropertyDescription(
      "Due date of the todo item representing the date after which the item will move to PAST_DUE state")
  private Instant dueAt;

  @Nullable
  @JsonPropertyDescription("The date at which the todo item was moved to DONE state")
  private Instant markedDoneAt;
}
