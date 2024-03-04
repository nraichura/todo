package com.simplesystem.todo.dto;

import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class ItemDto {

    private String description;
    private ItemStatus status = ItemStatus.NOT_DONE;
    private Instant createdAt = Instant.now();
    private Instant dueAt;
    private Instant markedDoneAt;

    public ItemDto(String description, Instant dueAt) {
        this.description = description;
        this.dueAt = dueAt;
    }
}
