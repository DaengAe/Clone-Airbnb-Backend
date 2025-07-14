package com.clone.Airbnb.jwt.service;

import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.jwt.provider.JwtProvider;
import com.clone.Airbnb.jwt.store.RefreshTokenStore;
import com.clone.Airbnb.web.user.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenStore refreshTokenStore;

    @Override
    public TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());
        refreshTokenStore.storeRefreshToken(user.getEmail(), refreshToken);

        return new TokenResponse(accessToken, refreshToken, user.getRole());
    }

    @Override
    public void discardRefreshToken(String email) {
        refreshTokenStore.removeRefreshToken(email);
    }
}
