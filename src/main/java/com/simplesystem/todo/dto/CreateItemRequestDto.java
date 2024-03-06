package com.simplesystem.todo.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.simplesystem.todo.entity.Item;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@JsonClassDescription("This represents the structure of request while creating the new todo item")
public class CreateItemRequestDto {
  @Nonnull
  @NotBlank(message = "Description cannot be blank")
  @JsonPropertyDescription("Description of the todo item")
  private String description;

  @Nonnull
  @JsonPropertyDescription(
      "Due date of the todo item representing the date after which the item will move to PAST_DUE state")
  private Instant dueAt;

  public Item toItemEntity() {
    return new Item(this.description, this.dueAt);
  }
}
