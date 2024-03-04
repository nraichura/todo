package com.simplesystem.todo.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "TODO_ITEM")
@Data
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private ItemStatus status;
    private Instant createdAt;
    private Instant dueAt;
    private Instant markedDoneAt;

}
