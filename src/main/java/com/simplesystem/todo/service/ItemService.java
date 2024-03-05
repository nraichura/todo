package com.simplesystem.todo.service;

import com.simplesystem.todo.dto.ItemDto;
import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.exception.HttpException;
import com.simplesystem.todo.repository.ItemRepository;
import com.simplesystem.todo.utility.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final TransactionHandler transactionHandler;

    public ItemDto changeDescription(final Long itemId, final String newDescription){
        return transactionHandler.runInTransaction(() -> {
                    Item existingItem = getItem(itemId);
                    existingItem.setDescription(newDescription);
                    return existingItem.toItemDto();
                });
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, String.format("Item with id %s not found", itemId)));
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public ItemDto changeStatus(final Long itemId, final ItemStatus status) {
        return transactionHandler.runInTransaction(() -> {
            Item existingItem = getItem(itemId);
            existingItem.setStatus(status);
            switch (status){
                case DONE -> existingItem.setMarkedDoneAt(Instant.now());
                case NOT_DONE -> existingItem.setMarkedDoneAt(null);
                case PAST_DUE -> throw new UnsupportedOperationException("Past Due operation is not supported at the moment");
            }
            return existingItem.toItemDto();
        });
    }

    public List<ItemDto> getItems(ItemStatus status) {
        Optional<ItemStatus> statusOptional = Optional.ofNullable(status);
        if (statusOptional.isPresent()){
            return itemRepository.findByStatus(statusOptional.get())
                    .stream()
                    .map(Item::toItemDto)
                    .toList();
        }
        return itemRepository.findAll()
                .stream()
                .map(Item::toItemDto)
                .toList();
    }
}
