package com.clone.Airbnb.config;

import com.clone.Airbnb.domain.user.service.CustomUserDetailsService;
import com.clone.Airbnb.jwt.filter.JwtAuthenticationFilter;
import com.clone.Airbnb.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtProvider jwtProvider; // JwtProvider 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight 요청 허용
                        .requestMatchers("/api/auth/**", "/api/login", "/api/logout", "/api/signup").permitAll() // 인증 관련 경로는 모두 허용
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // 관리자 경로는 ADMIN 역할 필요
                        .requestMatchers("/api/accommodations/register", "/api/accommodations/*/status").hasRole("HOST") // 숙소 등록 및 상태 조회는 HOST 역할 필요
                        .requestMatchers("/api/**").authenticated() // 그 외 /api/** 경로는 인증 필요
                        .anyRequest().permitAll() // 그 외 모든 요청은 허용
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, customUserDetailsService), UsernamePasswordAuthenticationFilter.class) // JWT 필터 추가
                .userDetailsService(customUserDetailsService)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
