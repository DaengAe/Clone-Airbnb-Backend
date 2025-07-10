package com.clone.Airbnb.web.user.controller;

import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.domain.user.service.UserService;
import com.clone.Airbnb.jwt.service.TokenService;
import com.clone.Airbnb.web.user.dto.LoginRequest;
import com.clone.Airbnb.web.user.dto.LogoutRequest;
import com.clone.Airbnb.web.user.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request);
        TokenResponse tokenResponse = tokenService.issueTokens(user);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody LogoutRequest request) {
        tokenService.discardRefreshToken(request.getEmail());
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
