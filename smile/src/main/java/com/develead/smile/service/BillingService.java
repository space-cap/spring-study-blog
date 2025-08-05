package com.develead.smile.service;
import com.develead.smile.domain.Billing;
import com.develead.smile.domain.BillingChangeLog;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.dto.BillingDto;
import com.develead.smile.repository.BillingChangeLogRepository;
import com.develead.smile.repository.BillingRepository;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class BillingService {
    private final BillingRepository billingRepository;
    private final UserAccountRepository userAccountRepository;
    private final BillingChangeLogRepository logRepository; // 추가

    public List<Billing> findAll() {
        return billingRepository.findAll();
    }

    public Optional<Billing> findById(Integer id) {
        return billingRepository.findById(id);
    }

    @Transactional
    public void updateBilling(BillingDto dto) {
        Billing billing = billingRepository.findById(dto.getBilling_id()).orElseThrow();
        UserAccount currentUser = getCurrentUser();

        // 변경 감지 및 로그
        logIfChanged(billing, "amountPaid", billing.getAmountPaid().toString(), dto.getAmountPaid().toString(), currentUser);
        logIfChanged(billing, "paymentMethod", billing.getPaymentMethod(), dto.getPaymentMethod(), currentUser);
        logIfChanged(billing, "paymentStatus", billing.getPaymentStatus(), dto.getPaymentStatus(), currentUser);

        billing.setAmountPaid(dto.getAmountPaid());
        billing.setPaymentMethod(dto.getPaymentMethod());
        billing.setPaymentStatus(dto.getPaymentStatus());
        if ("PAID".equals(dto.getPaymentStatus()) && billing.getPaymentDate() == null) {
            billing.setPaymentDate(LocalDateTime.now());
        }
        billing.setUpdatedBy(currentUser.getUser_account_id());

        billingRepository.save(billing);
    }

    private void logIfChanged(Billing billing, String fieldName, String oldValue, String newValue, UserAccount user) {
        if (!Objects.equals(oldValue, newValue)) {
            BillingChangeLog log = new BillingChangeLog();
            log.setBilling(billing);
            log.setFieldName(fieldName);
            log.setPreviousValue(oldValue);
            log.setNewValue(newValue);
            log.setChangedBy(user);
            logRepository.save(log);
        }
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
