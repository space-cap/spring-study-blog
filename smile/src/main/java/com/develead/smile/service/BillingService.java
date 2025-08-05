package com.develead.smile.service;
import com.develead.smile.domain.Billing;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.dto.BillingDto;
import com.develead.smile.repository.BillingRepository;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class BillingService {
    private final BillingRepository billingRepository;
    private final UserAccountRepository userAccountRepository;

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

        billing.setAmountPaid(dto.getAmountPaid());
        billing.setPaymentMethod(dto.getPaymentMethod());
        billing.setPaymentStatus(dto.getPaymentStatus());
        if ("PAID".equals(dto.getPaymentStatus())) {
            billing.setPaymentDate(LocalDateTime.now());
        }
        billing.setUpdatedBy(currentUser.getUser_account_id());

        billingRepository.save(billing);
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
