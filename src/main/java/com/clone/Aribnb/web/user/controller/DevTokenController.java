package com.clone.Aribnb.web.user.controller;

import com.clone.Aribnb.jwt.RefreshTokenStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevTokenController {

    private final RefreshTokenStore refreshTokenStore;

    @GetMapping("/refresh-tokens")
    public Map<String, String> getAllTokens() {
        return refreshTokenStore.getAllTokens(); // 아래 메서드 참고
    }
}
