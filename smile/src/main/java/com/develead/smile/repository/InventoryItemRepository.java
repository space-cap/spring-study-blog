package com.develead.smile.repository;
import com.develead.smile.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {}
