package com.simplesystem.todo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ItemDto {
    @Nonnull
    private Long id;
    @Nonnull
    private String description;
    @Nonnull
    private ItemStatus status;
    @Nonnull
    private Instant createdAt;
    @Nonnull
    private Instant dueAt;
    @Nullable
    private Instant markedDoneAt;
}
