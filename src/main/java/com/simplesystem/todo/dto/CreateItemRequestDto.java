package com.simplesystem.todo.dto;

import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CreateItemRequestDto {
    @Nonnull
    @NotBlank(message = "Description cannot be blank")
    private String description;
    @Nonnull
    private Instant dueAt;

    public Item toItemEntity() {
        return Item.builder()
                .description(this.description)
                .dueAt(this.dueAt)
                .build();
    }
}
