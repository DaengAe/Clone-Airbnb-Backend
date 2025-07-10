package com.clone.Airbnb.jwt.service;

import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.web.user.dto.TokenResponse;

public interface TokenService {
    TokenResponse issueTokens(User user);
    void discardRefreshToken(String email);
}
