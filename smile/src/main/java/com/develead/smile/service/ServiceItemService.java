package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.lang.reflect.Field;
import java.math.BigDecimal;
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
    public ServiceItem save(ServiceItem serviceItem) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAccount currentUser = userAccountRepository.findByLoginId(username).orElse(null);

        if (serviceItem.getService_item_id() == null) { // 새로 생성
            ServiceItem savedItem = serviceItemRepository.save(serviceItem);
            logChange(savedItem, "ALL", null, "Created", ServiceItemChangeLog.ChangeType.INSERT, currentUser);
            return savedItem;
        } else { // 수정
            ServiceItem existingItem = serviceItemRepository.findById(serviceItem.getService_item_id()).orElseThrow();

            // 변경 감지 및 로그 기록
            checkAndLogChange(existingItem, serviceItem, currentUser);

            serviceItem.setUpdatedAt(LocalDateTime.now());
            return serviceItemRepository.save(serviceItem);
        }
    }

    private void checkAndLogChange(ServiceItem oldItem, ServiceItem newItem, UserAccount user) {
        // BigDecimal, String, Integer, char 타입의 주요 필드 변경을 감지합니다.
        compareAndLog(oldItem.getDefaultCost(), newItem.getDefaultCost(), "defaultCost", oldItem, user);
        compareAndLog(oldItem.getServiceName(), newItem.getServiceName(), "serviceName", oldItem, user);
        compareAndLog(oldItem.getCategory(), newItem.getCategory(), "category", oldItem, user);
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
