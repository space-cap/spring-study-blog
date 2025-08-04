package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
@Service @RequiredArgsConstructor
public class InventoryService {
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final UserAccountRepository userAccountRepository;
    private final ClinicRepository clinicRepository;

    public List<InventoryItem> findAll() { return inventoryItemRepository.findAll(); }
    public Optional<InventoryItem> findById(Integer id) { return inventoryItemRepository.findById(id); }

    @Transactional
    public InventoryItem save(InventoryItem item) {
        UserAccount currentUser = getCurrentUser();
        // 임시로 기본 Clinic 설정 (실제로는 사용자 소속 Clinic 등으로 설정해야 함)
        Clinic clinic = clinicRepository.findById(1).orElseThrow();
        item.setClinic(clinic);

        if (item.getItem_id() == null) { // 신규 등록
            item.setCreatedBy(currentUser.getUser_account_id());
            item.setUpdatedBy(currentUser.getUser_account_id());
            InventoryItem savedItem = inventoryItemRepository.save(item);
            logChange(savedItem, "CREATE", item.getQuantity(), "신규 등록", currentUser.getUser_account_id());
            return savedItem;
        } else { // 수정
            InventoryItem existingItem = inventoryItemRepository.findById(item.getItem_id()).orElseThrow();
            int quantityChange = item.getQuantity() - existingItem.getQuantity();

            existingItem.setItemName(item.getItemName());
            existingItem.setItemCode(item.getItemCode());
            existingItem.setQuantity(item.getQuantity());
            existingItem.setUnit(item.getUnit());
            existingItem.setSafeStockLevel(item.getSafeStockLevel());
            existingItem.setUpdatedBy(currentUser.getUser_account_id());

            InventoryItem updatedItem = inventoryItemRepository.save(existingItem);

            if (quantityChange != 0) {
                String changeType = quantityChange > 0 ? "IN" : "OUT";
                logChange(updatedItem, changeType, quantityChange, "관리자 수정", currentUser.getUser_account_id());
            }
            return updatedItem;
        }
    }

    private void logChange(InventoryItem item, String changeType, int quantityChanged, String reason, Integer userId) {
        InventoryLog log = new InventoryLog();
        log.setItem(item);
        log.setChangeType(changeType);
        log.setQuantityChanged(quantityChanged);
        log.setReason(reason);
        log.setLoggedBy(userId);
        inventoryLogRepository.save(log);
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
