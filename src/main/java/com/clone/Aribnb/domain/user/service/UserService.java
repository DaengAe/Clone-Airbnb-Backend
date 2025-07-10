package com.clone.Aribnb.domain.user.service;

import com.clone.Aribnb.domain.user.User;
import com.clone.Aribnb.web.user.dto.LoginRequest;
import com.clone.Aribnb.web.user.dto.SignupRequest;

public interface UserService {
    User login(LoginRequest loginRequest);
    void signup(SignupRequest signupRequest);
}
