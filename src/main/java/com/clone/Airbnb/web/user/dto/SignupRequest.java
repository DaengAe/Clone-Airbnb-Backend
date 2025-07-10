package com.clone.Airbnb.web.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class SignupRequest {

    @NotBlank(message = "{signup.name.blank}")
    private String name;

    @NotBlank(message = "{signup.email.blank}")
    @Email(message = "{signup.email.invalid}")
    private String email;

    @NotBlank(message = "{signup.password.blank}")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "{signup.password.invalid}"
    )
    private String password;

    @NotBlank(message = "{signup.confirmPassword.blank}")
    private String confirmPassword;

    public boolean passwordsMatch() {
        return this.password != null && this.password.equals(this.confirmPassword);
    }
}
