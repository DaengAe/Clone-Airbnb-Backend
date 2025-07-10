package com.clone.Aribnb.web.user.controller;

import com.clone.Aribnb.domain.user.User;
import com.clone.Aribnb.domain.user.service.UserService;
import com.clone.Aribnb.jwt.JwtProvider;
import com.clone.Aribnb.jwt.RefreshTokenStore;
import com.clone.Aribnb.web.user.dto.LoginRequest;
import com.clone.Aribnb.web.user.dto.TokenResponse;
import com.clone.Aribnb.web.user.dto.LogoutRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {

        User loginUser = userService.login(request.getEmail(), request.getPassword());

        if (loginUser == null) {
            return ResponseEntity
                    .badRequest()
                    .body("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String accessToken = jwtProvider.generateAccessToken(loginUser.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(loginUser.getEmail());

        // Refresh 토큰 저장 (DB or 인메모리)
        refreshTokenStore.storeRefreshToken(loginUser.getEmail(), refreshToken);


        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        refreshTokenStore.removeRefreshToken(request.getEmail());
        return ResponseEntity.ok("로그아웃 완료");
    }
}
