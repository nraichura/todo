package com.example.todo.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TODO_ITEM")
public class Item {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "DESCRIPTION", nullable = false)
  private String description;

  @Column(name = "STATUS", nullable = false)
  private ItemStatus status = ItemStatus.NOT_DONE;

  @Column(name = "CREATED_AT", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "DUE_AT", nullable = false)
  private Instant dueAt;

  @Column(name = "MARKED_DONE_AT")
  private Instant markedDoneAt = null;

  public Item(String description, Instant dueAt) {
    this.description = description;
    this.dueAt = dueAt;
  }
}
