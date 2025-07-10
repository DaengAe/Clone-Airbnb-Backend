package com.clone.Aribnb.jwt;

import com.clone.Aribnb.domain.user.User;
import com.clone.Aribnb.web.user.dto.TokenResponse;

public interface TokenService {
    TokenResponse issueTokens(User user);
    void discardRefreshToken(String email);
}
