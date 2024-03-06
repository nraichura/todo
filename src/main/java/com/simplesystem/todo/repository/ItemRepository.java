package com.simplesystem.todo.repository;

import com.simplesystem.todo.entity.Item;
import com.simplesystem.todo.entity.ItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
  List<Item> findByStatus(ItemStatus status);
}
