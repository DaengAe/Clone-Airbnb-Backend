package com.clone.Airbnb.domain.user.service;

import com.clone.Airbnb.domain.user.entity.User;
import com.clone.Airbnb.web.user.dto.LoginRequest;
import com.clone.Airbnb.web.user.dto.SignupRequest;

public interface UserService {
    User login(LoginRequest loginRequest);
    void signup(SignupRequest signupRequest);
}
