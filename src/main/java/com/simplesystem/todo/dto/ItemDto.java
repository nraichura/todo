package com.simplesystem.todo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    @Nonnull @NotBlank
    private String description;
    @Nonnull
    private ItemStatus status;
    @Nonnull
    private Instant createdAt;
    @Nonnull
    private Instant dueAt;
    @Nullable
    private Instant markedDoneAt;

    public ItemDto(String description, Instant dueAt) {
        this.description = description;
        this.dueAt = dueAt;
        this.status = ItemStatus.NOT_DONE;
        this.createdAt = Instant.now();
        this.markedDoneAt = null;
    }


    public Item toItemEntity() {
        return Item.builder()
                .description(this.description)
                .createdAt(this.createdAt)
                .dueAt(this.dueAt)
                .markedDoneAt(this.markedDoneAt)
                .status(this.status)
                .build();
    }
}
