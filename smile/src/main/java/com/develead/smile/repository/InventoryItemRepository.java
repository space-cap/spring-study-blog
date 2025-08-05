package com.develead.smile.repository;
import com.develead.smile.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
    // [수정] 통계를 위한 쿼리 추가
    @Query("SELECT count(i) FROM InventoryItem i WHERE i.quantity < i.safeStockLevel")
    long countLowStockItems();
}
