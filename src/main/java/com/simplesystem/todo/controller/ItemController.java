package com.simplesystem.todo.controller;

import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.repository.ItemRepository;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class ItemController {

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto saveItem(@RequestBody @Nonnull @Valid ItemDto item){
        return itemRepository.save(item.toItemEntity()).toItemDto();
    }
}
