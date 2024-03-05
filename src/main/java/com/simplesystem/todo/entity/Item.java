package com.simplesystem.todo.entity;


import com.simplesystem.todo.dto.ItemDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "TODO_ITEM")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DESCRIPTION", nullable = false)
    private String description;
    @Column(name = "STATUS", nullable = false)
    private ItemStatus status;
    @Column(name = "CREATED_AT", nullable = false)
    private Instant createdAt;
    @Column(name= "DUE_AT", nullable = false)
    private Instant dueAt;
    @Column(name = "MARKED_DONE_AT")
    private Instant markedDoneAt;

    public ItemDto toItemDto() {
        return ItemDto.builder()
                .description(this.description)
                .status(this.status)
                .createdAt(this.createdAt)
                .dueAt(this.dueAt)
                .markedDoneAt(this.markedDoneAt)
                .build();
    }
}
