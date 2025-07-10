package com.clone.Aribnb.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "{login.email.blank}")
    @Email(message = "{login.email.invalid}")
    private String email;

    @NotBlank(message = "{login.password.blank}")
    @Size(min = 8, message = "{login.password.tooShort}")
    private String password;
}