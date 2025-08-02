package com.develead.smile.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // [수정] 정적 리소스(css, js 등)에 대한 접근을 허용 (권장 방식)
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // 홈, 회원가입, 로그인 페이지는 모두 허용
                        .requestMatchers("/", "/register", "/login").permitAll()
                        // 관리자 페이지는 'ADMIN' 역할만 접근 가능
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증된 사용자만 접근 가능
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }
}