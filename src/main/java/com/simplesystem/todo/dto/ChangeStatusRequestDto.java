package com.simplesystem.todo.dto;

import com.simplesystem.todo.entity.ItemStatus;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;

public record ChangeStatusRequestDto(@Nonnull ItemStatus status) {
}
