package com.simplesystem.todo.service;

import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import com.simplesystem.todo.repository.ItemRepository;
import com.simplesystem.todo.utility.TransactionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class UpdatePastDueStatusScheduler {

  private final ItemRepository itemRepository;
  private final TransactionHandler transactionHandler;

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
