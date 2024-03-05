package com.simplesystem.todo.controller;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.repository.ItemRepository;
import com.simplesystem.todo.service.ItemService;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto saveItem(@RequestBody @Nonnull @Valid CreateItemRequestDto item){
        return itemService.save(item.toItemEntity()).toItemDto();
    }

    @PatchMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto changeDescription(@RequestBody @Nonnull @Valid ChangeDescriptionRequestDto item, @PathVariable("itemId") Long itemId){
        return itemService.changeDescription(itemId, item.description());
    }
}
