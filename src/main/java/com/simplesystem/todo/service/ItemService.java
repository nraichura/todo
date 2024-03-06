package com.simplesystem.todo.service;

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

    public Item changeDescription(final Long itemId, final String newDescription) {
        return transactionHandler.runInTransaction(() -> {
            Item existingItem = getItem(itemId);
            existingItem.setDescription(newDescription);
            return existingItem;
        });
    }

    public Item getItem(Long itemId) {
        return transactionHandler.runInTransaction(() -> itemRepository.findById(itemId).orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, String.format("Item with id %s not found", itemId))));
    }

    public Item save(Item item) {
        return transactionHandler.runInTransaction(() -> itemRepository.save(item));
    }

    public Item changeStatus(final Long itemId, final ItemStatus status) {
        return transactionHandler.runInTransaction(() -> {
            Item existingItem = getItem(itemId);
            existingItem.setStatus(status);
            switch (status) {
                case DONE -> existingItem.setMarkedDoneAt(Instant.now());
                case NOT_DONE -> existingItem.setMarkedDoneAt(null);
                case PAST_DUE -> throw new HttpException(HttpStatus.BAD_REQUEST, String.format("Changing item status for the item id '%s' to 'Past Due' is not allowed.", existingItem.getId()));
            }
            return existingItem;
        });
    }

    public List<Item> getItems(ItemStatus statusToExclude) {
        return transactionHandler.runInTransaction(() -> {
            Optional<ItemStatus> statusOptional = Optional.ofNullable(statusToExclude);
            if (statusOptional.isPresent()) {
                return itemRepository.findByStatus(statusOptional.get());
            }
            return itemRepository.findAll();
        });
    }
}
