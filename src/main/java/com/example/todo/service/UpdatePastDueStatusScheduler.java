package com.example.todo.service;

import com.example.todo.entity.Item;
import com.example.todo.repository.ItemRepository;
import com.example.todo.utility.TransactionHandler;
import com.example.todo.entity.ItemStatus;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdatePastDueStatusScheduler {

  private final ItemRepository itemRepository;
  private final TransactionHandler transactionHandler;

  /**
   * Runs at a fixed delay of specified configuration at [updatePastDueStatus.scheduler.fixedDelay]
   * and change the item status to 'Past Due' for the items that are past their due date.
   */
  @Scheduled(fixedDelayString = "${updatePastDueStatus.scheduler.fixedDelay}")
  public void updatePastDueStatus() {
    transactionHandler.runInTransaction(
        () -> {
          itemRepository.findAll().stream()
              .filter(this::isItemAlreadyDue)
              .forEach(item -> item.setStatus(ItemStatus.PAST_DUE));
          return null;
        });
  }

  private boolean isItemAlreadyDue(Item item) {
    return item.getDueAt().isBefore(Instant.now());
  }
}
