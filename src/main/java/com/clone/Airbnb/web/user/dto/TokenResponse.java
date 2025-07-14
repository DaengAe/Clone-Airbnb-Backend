package com.clone.Airbnb.web.user.dto;

import com.clone.Airbnb.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private UserRole role;
}
