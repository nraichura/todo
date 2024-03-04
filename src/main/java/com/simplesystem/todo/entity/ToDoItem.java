package com.simplesystem.todo.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "TODO_ITEM")
@Data
public class ToDoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    private ToDoItemStatus status;
    private Instant createdAt;
    private Instant dueAt;
    private Instant markedDoneAt;

    private enum ToDoItemStatus{
        NOT_DONE("Not Done"),
        DONE("Done"),
        PAST_DUE("Past Due");

        private final String description;

        ToDoItemStatus(final String description) {
            this.description = description;
        }
    }
}
