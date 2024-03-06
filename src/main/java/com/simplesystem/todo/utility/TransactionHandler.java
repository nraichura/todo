package com.simplesystem.todo.utility;

import com.simplesystem.todo.exception.HttpException;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionHandler {
  @Transactional(
      propagation = Propagation.REQUIRED,
      rollbackFor = {HttpException.class})
  public <T> T runInTransaction(Supplier<T> supplier) {
    return supplier.get();
  }
}
