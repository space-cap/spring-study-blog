package com.develead.smile.service;

import com.develead.smile.domain.Customer;
import com.develead.smile.domain.Role;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.dto.UserRegistrationDto;
import com.develead.smile.repository.CustomerRepository;
import com.develead.smile.repository.RoleRepository;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserAccountRepository userAccountRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerNewUser(UserRegistrationDto registrationDto) {
        if (userAccountRepository.findByLoginId(registrationDto.getLoginId()).isPresent()) {
            throw new IllegalStateException("이미 사용 중인 아이디입니다.");
        }

        Role customerRole = roleRepository.findByRoleName("ROLE_CUSTOMER")
                .orElseThrow(() -> new RuntimeException("Error: CUSTOMER role not found."));

        UserAccount userAccount = new UserAccount();
        userAccount.setLoginId(registrationDto.getLoginId());
        userAccount.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        userAccount.setRole(customerRole);

        Customer customer = new Customer();
        customer.setName(registrationDto.getName());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        customer.setUserAccount(userAccount);

        customerRepository.save(customer);
    }
}