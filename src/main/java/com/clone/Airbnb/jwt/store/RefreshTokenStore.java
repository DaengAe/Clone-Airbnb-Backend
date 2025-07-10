package com.clone.Airbnb.jwt.store;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RefreshTokenStore {
    private final ConcurrentHashMap<String, String> tokenStore = new ConcurrentHashMap<>();

    public void storeRefreshToken(String email, String token) {
        tokenStore.put(email, token);
    }

    public boolean validateRefreshToken(String email, String token) {
        return token.equals(tokenStore.get(email));
    }

    public void removeRefreshToken(String email) {
        tokenStore.remove(email);
    }

    public Map<String, String> getAllTokens() {
        return new HashMap<>(tokenStore); // 복사본 리턴
    }
}

