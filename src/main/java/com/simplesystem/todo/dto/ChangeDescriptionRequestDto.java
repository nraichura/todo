package com.simplesystem.todo.dto;

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

@JsonClassDescription("This represents the structure of request for changing the description of the todo item")
public record ChangeDescriptionRequestDto(@JsonPropertyDescription("Description of the todo item") @Nonnull @NotBlank String description){}
