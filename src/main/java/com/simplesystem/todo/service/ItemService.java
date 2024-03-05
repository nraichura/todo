package com.simplesystem.todo.service;

import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.exception.HttpException;
import com.simplesystem.todo.repository.ItemRepository;
import com.simplesystem.todo.utility.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TransactionHandler transactionHandler;

    public ItemDto changeDescription(final Long itemId, final String newDescription){
        return transactionHandler.runInTransaction(() -> {
                    Item existingItem = itemRepository.findById(itemId).orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, String.format("Item with id %s not found", itemId)));
                    existingItem.setDescription(newDescription);
                    return existingItem.toItemDto();
                });
    }
}
