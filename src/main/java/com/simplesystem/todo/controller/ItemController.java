package com.simplesystem.todo.controller;

import com.simplesystem.todo.dto.ChangeDescriptionRequestDto;
import com.simplesystem.todo.dto.ChangeStatusRequestDto;
import com.simplesystem.todo.dto.CreateItemRequestDto;
import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.exception.GenericErrorModel;
import com.simplesystem.todo.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create new todo item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created todo item"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = GenericErrorModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = GenericErrorModel.class)))
    })
    public ItemDto saveItem(@RequestBody @Nonnull @Valid CreateItemRequestDto item){
        return itemService.save(item.toItemEntity()).toItemDto();
    }

    @PatchMapping("/items/{itemId}/description")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update todo item to change description")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated todo item"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = GenericErrorModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = GenericErrorModel.class)))
    })
    public ItemDto changeDescription(@RequestBody @Nonnull @Valid ChangeDescriptionRequestDto item, @Nonnull @PathVariable("itemId") Long itemId){
        return itemService.changeDescription(itemId, item.description()).toItemDto();
    }

    @PatchMapping("/items/{itemId}/status")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update todo item to change status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated todo item"),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = GenericErrorModel.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = GenericErrorModel.class)))
    })
    public ItemDto changeStatus(@RequestBody @Nonnull ChangeStatusRequestDto item, @Nonnull @PathVariable("itemId") Long itemId){
        return itemService.changeStatus(itemId, item.status()).toItemDto();
    }

    @GetMapping("/items")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get todo items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got todo items"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = GenericErrorModel.class)))
    })
    public List<ItemDto> getItems(@RequestParam(name = "status", required = false) @Nullable ItemStatus status){
        return itemService.getItems(status).stream()
                .map(Item::toItemDto).toList();
    }

    @GetMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the specific todo item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully got the todo item"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = GenericErrorModel.class)))
    })
    public ItemDto getItem(@Nonnull @PathVariable("itemId") Long itemId){
        return itemService.getItem(itemId).toItemDto();
    }
}
