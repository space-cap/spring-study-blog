package com.fastcampus.ch2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * HTTP 보안 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 관리자 페이지는 ADMIN 권한 필요
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // H2 콘솔 접근 허용 (개발용)
                        .requestMatchers("/h2-console/**").hasRole("ADMIN")
                        .requestMatchers("/error").permitAll()  // 에러 페이지 접근 허용
                        // 랜딩 페이지와 정적 리소스는 모두 허용
                        .requestMatchers("/landing/**", "/css/**", "/js/**", "/images/**").permitAll()
                        // 그 외 모든 요청은 허용
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        // 커스텀 로그인 페이지 설정
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1) // 동시 세션 1개로 제한
                        .maxSessionsPreventsLogin(false)
                )
                // H2 콘솔 사용을 위한 설정
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                        .ignoringRequestMatchers("/landing/**")
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions
                                .sameOrigin()
                        )
                );

        return http.build();
    }

    /**
     * 사용자 정보 서비스 (메모리 기반)
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails manager = User.builder()
                .username("manager")
                .password(passwordEncoder().encode("manager123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin, manager);
    }

    /**
     * 비밀번호 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
