package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service @RequiredArgsConstructor
public class ServiceItemService {
    private final ServiceItemRepository serviceItemRepository;
    private final ServiceItemChangeLogRepository logRepository;
    private final UserAccountRepository userAccountRepository;

    public List<ServiceItem> findAll() { return serviceItemRepository.findAll(); }
    public Optional<ServiceItem> findById(Integer id) { return serviceItemRepository.findById(id); }

    @Transactional
    public ServiceItem create(ServiceItem serviceItem) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount currentUser = userAccountRepository.findByLoginId(username).orElse(null);

        ServiceItem savedItem = serviceItemRepository.save(serviceItem);
        logChange(savedItem, "ALL", null, "Created", ServiceItemChangeLog.ChangeType.INSERT, currentUser);
        return savedItem;
    }

    @Transactional
    public ServiceItem update(ServiceItem updatedItem) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount currentUser = userAccountRepository.findByLoginId(username).orElse(null);

        ServiceItem existingItem = serviceItemRepository.findById(updatedItem.getService_item_id()).orElseThrow();

        // 변경 감지 및 로그 기록
        checkAndLogChanges(existingItem, updatedItem, currentUser);

        // 기존 엔티티에 변경된 값만 업데이트
        existingItem.setServiceCode(updatedItem.getServiceCode());
        existingItem.setServiceName(updatedItem.getServiceName());
        existingItem.setCategory(updatedItem.getCategory());
        existingItem.setDefaultCost(updatedItem.getDefaultCost());
        existingItem.setDescription(updatedItem.getDescription());
        existingItem.setIsInsuranceCovered(updatedItem.getIsInsuranceCovered());
        existingItem.setIsActive(updatedItem.getIsActive());
        existingItem.setUpdatedAt(LocalDateTime.now());

        return serviceItemRepository.save(existingItem);
    }

    private void checkAndLogChanges(ServiceItem oldItem, ServiceItem newItem, UserAccount user) {
        compareAndLog(oldItem.getServiceCode(), newItem.getServiceCode(), "serviceCode", oldItem, user);
        compareAndLog(oldItem.getServiceName(), newItem.getServiceName(), "serviceName", oldItem, user);
        compareAndLog(oldItem.getCategory(), newItem.getCategory(), "category", oldItem, user);
        compareAndLog(oldItem.getDefaultCost(), newItem.getDefaultCost(), "defaultCost", oldItem, user);
        compareAndLog(oldItem.getIsInsuranceCovered(), newItem.getIsInsuranceCovered(), "isInsuranceCovered", oldItem, user);
        compareAndLog(oldItem.getIsActive(), newItem.getIsActive(), "isActive", oldItem, user);
    }

    private void compareAndLog(Object oldValue, Object newValue, String fieldName, ServiceItem item, UserAccount user) {
        if (!Objects.equals(oldValue, newValue)) {
            logChange(item, fieldName,
                    oldValue != null ? oldValue.toString() : null,
                    newValue != null ? newValue.toString() : "",
                    ServiceItemChangeLog.ChangeType.UPDATE, user);
        }
    }

    private void logChange(ServiceItem item, String fieldName, String prev, String next, ServiceItemChangeLog.ChangeType type, UserAccount user) {
        ServiceItemChangeLog log = new ServiceItemChangeLog();
        log.setServiceItem(item);
        log.setFieldName(fieldName);
        log.setPreviousValue(prev);
        log.setNewValue(next);
        log.setChangeType(type);
        if (user != null) {
            log.setChangedBy(user);
            log.setChangedByName(user.getLoginId());
        }
        logRepository.save(log);
    }
}
