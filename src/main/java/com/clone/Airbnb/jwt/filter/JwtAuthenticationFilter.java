package com.clone.Airbnb.jwt.filter;

import com.clone.Airbnb.jwt.provider.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("인증 헤더: " + authorizationHeader);
        String token = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // "Bearer " 다음부터 잘라냄
            System.out.println("파싱된 토큰: " + token);
        }

        if (token != null) {
            if (jwtProvider.validateToken(token)) {
                System.out.println("토큰 검증 됨.");
                String userEmail = jwtProvider.getUserEmail(token);
                System.out.println("사용자 이메일 from 토큰: " + userEmail);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("인증 객체가 SecurityContext에 저장됨.");
            } else {
                System.out.println("토큰이 유효하지 않습니다.");
            }
        } else {
            System.out.println("인증 헤더에서 토큰을 찾을 수 없습니다.");
        }

        filterChain.doFilter(request, response);
    }
}
