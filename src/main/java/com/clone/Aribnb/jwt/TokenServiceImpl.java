package com.clone.Aribnb.jwt;

import com.clone.Aribnb.domain.user.User;
import com.clone.Aribnb.web.user.dto.TokenResponse;
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

        return new TokenResponse(accessToken, refreshToken);
    }

    @Override
    public void discardRefreshToken(String email) {
        refreshTokenStore.removeRefreshToken(email);
    }
}
