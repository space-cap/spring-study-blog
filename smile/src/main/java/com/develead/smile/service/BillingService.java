package com.develead.smile.service;
import com.develead.smile.domain.*;
import com.develead.smile.dto.PaymentTransactionDto;
import com.develead.smile.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class BillingService {
    private final BillingRepository billingRepository;
    private final UserAccountRepository userAccountRepository;
    private final PaymentTransactionRepository transactionRepository;

    public List<Billing> findAll() { return billingRepository.findAll(); }
    public Optional<Billing> findById(Integer id) { return billingRepository.findById(id); }

    @Transactional
    public void addPayment(Integer billingId, PaymentTransactionDto transactionDto) {
        Billing billing = billingRepository.findById(billingId).orElseThrow();
        UserAccount currentUser = getCurrentUser();

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setBilling(billing);
        transaction.setAmount(transactionDto.getAmount());
        transaction.setPaymentMethod(transactionDto.getPaymentMethod());
        transaction.setCreatedBy(currentUser.getUser_account_id());
        transactionRepository.save(transaction);

        // 청구서 정보 업데이트
        billing.setTotalPaid(billing.getTotalPaid().add(transaction.getAmount()));
        billing.setBalance(billing.getTotalAmount().subtract(billing.getTotalPaid()));
        billing.setUpdatedBy(currentUser.getUser_account_id());

        if (billing.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            billing.setBillingStatus("PAID");
        } else {
            billing.setBillingStatus("PARTIAL");
        }
        billingRepository.save(billing);
    }

    private UserAccount getCurrentUser() {
        String loginId = SecurityContextHolder.getContext().getAuthentication().getName();
        return userAccountRepository.findByLoginId(loginId).orElseThrow();
    }
}
