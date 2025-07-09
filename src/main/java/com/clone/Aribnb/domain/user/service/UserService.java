package com.clone.Aribnb.domain.user.service;

import com.clone.Aribnb.domain.user.User;

public interface UserService {
    User login(String email, String password);
    String encodePassword(String rawPassword);
}
