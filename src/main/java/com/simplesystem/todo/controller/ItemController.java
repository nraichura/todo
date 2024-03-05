package com.simplesystem.todo.controller;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.ChangeStatusRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.service.ItemService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PatchMapping("/items/{itemId}/description")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto changeDescription(@RequestBody @Nonnull @Valid ChangeDescriptionRequestDto item, @Nonnull @PathVariable("itemId") Long itemId){
        return itemService.changeDescription(itemId, item.description()).toItemDto();
    }

    @PatchMapping("/items/{itemId}/status")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto changeStatus(@RequestBody @Nonnull ChangeStatusRequestDto item, @Nonnull @PathVariable("itemId") Long itemId){
        return itemService.changeStatus(itemId, item.status()).toItemDto();
    }

    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getItems(@RequestParam(name = "statusToExclude", required = false) @Nullable ItemStatus statusToExclude){
        return itemService.getItems(statusToExclude).stream()
                .map(Item::toItemDto).toList();
    }

    @GetMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto getItem(@Nonnull @PathVariable("itemId") Long itemId){
        return itemService.getItem(itemId).toItemDto();
    }
}
