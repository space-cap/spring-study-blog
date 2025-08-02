package com.develead.smile.service;

import com.develead.smile.domain.UserAccount;
import com.develead.smile.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + username));

        GrantedAuthority authority = new SimpleGrantedAuthority(userAccount.getRole().getRoleName());

        return new User(userAccount.getLoginId(), userAccount.getPasswordHash(), Collections.singleton(authority));
    }
}
