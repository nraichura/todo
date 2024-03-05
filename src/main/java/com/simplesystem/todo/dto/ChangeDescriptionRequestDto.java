package com.simplesystem.todo.dto;

import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record ChangeDescriptionRequestDto(@Nonnull @NotBlank String description){}
