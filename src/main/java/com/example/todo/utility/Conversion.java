package com.example.todo.utility;

import com.example.todo.dto.CreateItemRequestDto;
import com.example.todo.entity.Item;
import com.example.todo.dto.ItemDto;

public class Conversion {

  private Conversion(){
    throw new UnsupportedOperationException("Object Instantiation is not supported");
  }
  public static Item dtoToDomain(final CreateItemRequestDto itemDto) {
    return new Item(itemDto.getDescription(), itemDto.getDueAt());
  }

  public static ItemDto domainToDto(final Item item) {
    return new ItemDto(
        item.getId(),
        item.getDescription(),
        item.getStatus(),
        item.getCreatedAt(),
        item.getDueAt(),
        item.getMarkedDoneAt());
  }
}
