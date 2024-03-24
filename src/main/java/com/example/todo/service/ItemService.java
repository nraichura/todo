package com.example.todo.service;

import com.example.todo.dto.CreateItemRequestDto;
import com.example.todo.entity.Item;
import com.example.todo.exception.HttpException;
import com.example.todo.repository.ItemRepository;
import com.example.todo.utility.Conversion;
import com.example.todo.utility.TransactionHandler;
import com.example.todo.dto.ItemDto;
import com.example.todo.entity.ItemStatus;
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

    public ItemDto changeDescription(final Long itemId, final String newDescription) {
        return Conversion.domainToDto(
                transactionHandler.runInTransaction(() -> {
                    Item existingItem = getItemEntity(itemId);
                    existingItem.setDescription(newDescription);
                    return existingItem;
                })
        );
    }

    public ItemDto getItem(Long itemId) {
        return Conversion.domainToDto(
                getItemEntity(itemId)
        );
    }

    private Item getItemEntity(Long itemId) {
        return transactionHandler.runInTransaction(
                () -> itemRepository.findById(itemId).orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND, String.format("Item with id %s not found", itemId)))
        );
    }

    public ItemDto save(final CreateItemRequestDto item) {
        return Conversion.domainToDto(
                transactionHandler.runInTransaction(() -> itemRepository.save(Conversion.dtoToDomain(item)))
        );
    }

    /**
     * changes the item status of the item
     * @param itemId The unique identifier of the item
     * @param status Item status
     * @return The item after the changed status
     */
    public ItemDto changeStatus(final Long itemId, final ItemStatus status) {
        return Conversion.domainToDto(
                transactionHandler.runInTransaction(() -> {
                    Item existingItem = getItemEntity(itemId);
                    existingItem.setStatus(status);
                    switch (status) {
                        case DONE -> existingItem.setMarkedDoneAt(Instant.now());
                        case NOT_DONE -> existingItem.setMarkedDoneAt(null);
                        case PAST_DUE -> throw new HttpException(HttpStatus.BAD_REQUEST, String.format("Changing item status for the item id '%s' to 'Past Due' is not allowed.", existingItem.getId()));
                    }
                    return existingItem;
                })
        );
    }

    public List<ItemDto> getItems(ItemStatus status) {
        List<Item> items = transactionHandler.runInTransaction(() -> {
            Optional<ItemStatus> statusOptional = Optional.ofNullable(status);
            if (statusOptional.isPresent()) {
                return itemRepository.findByStatus(statusOptional.get());
            }
            return itemRepository.findAll();
        });
        return items.stream().map(Conversion::domainToDto).toList();
    }
}
