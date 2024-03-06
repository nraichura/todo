package com.simplesystem.todo.utility;

import com.simplesystem.todo.exception.HttpException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

@Service
public class TransactionHandler {
  @Transactional(
      propagation = Propagation.REQUIRED,
      rollbackFor = {HttpException.class})
  public <T> T runInTransaction(Supplier<T> supplier) {
    return supplier.get();
  }
}
