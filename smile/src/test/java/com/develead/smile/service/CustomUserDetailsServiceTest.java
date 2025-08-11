package com.develead.smile.service;

import com.develead.smile.domain.Role;
import com.develead.smile.domain.UserAccount;
import com.develead.smile.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private UserAccount userAccount;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setRole_id(1);
        role.setRoleName("USER");

        userAccount = new UserAccount();
        userAccount.setUser_account_id(1);
        userAccount.setLoginId("test@example.com");
        userAccount.setPasswordHash("$2a$10$hashedPassword");
        userAccount.setRole(role);
    }

    @Test
    @DisplayName("사용자 인증 - 성공")
    void loadUserByUsername_Success() {
        // Given
        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("$2a$10$hashedPassword", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("USER")));
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    @DisplayName("사용자 인증 - 관리자 권한")
    void loadUserByUsername_AdminRole() {
        // Given
        Role adminRole = new Role();
        adminRole.setRole_id(2);
        adminRole.setRoleName("ADMIN");

        UserAccount adminAccount = new UserAccount();
        adminAccount.setUser_account_id(2);
        adminAccount.setLoginId("admin@example.com");
        adminAccount.setPasswordHash("$2a$10$adminPassword");
        adminAccount.setRole(adminRole);

        when(userAccountRepository.findByLoginId("admin@example.com")).thenReturn(Optional.of(adminAccount));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ADMIN")));
    }

    @Test
    @DisplayName("사용자 인증 - 존재하지 않는 사용자")
    void loadUserByUsername_UserNotFound() {
        // Given
        when(userAccountRepository.findByLoginId("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername("nonexistent@example.com"));
        
        assertEquals("User not found with loginId: nonexistent@example.com", exception.getMessage());
    }

    @Test
    @DisplayName("사용자 인증 - 빈 문자열 사용자명")
    void loadUserByUsername_EmptyUsername() {
        // Given
        when(userAccountRepository.findByLoginId("")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername(""));
    }

    @Test
    @DisplayName("사용자 인증 - null 사용자명")
    void loadUserByUsername_NullUsername() {
        // Given
        when(userAccountRepository.findByLoginId(null)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class,
            () -> customUserDetailsService.loadUserByUsername(null));
    }

    @Test
    @DisplayName("사용자 인증 - 권한 정보 검증")
    void loadUserByUsername_AuthorityValidation() {
        // Given
        when(userAccountRepository.findByLoginId("test@example.com")).thenReturn(Optional.of(userAccount));

        // When
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails.getAuthorities());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("USER", userDetails.getAuthorities().iterator().next().getAuthority());
    }
}