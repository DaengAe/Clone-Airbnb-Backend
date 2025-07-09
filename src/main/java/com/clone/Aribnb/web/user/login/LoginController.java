package com.clone.Aribnb.web.user.login;

import com.clone.Aribnb.domain.user.User;
import com.clone.Aribnb.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LoginController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, BindingResult bindingResult) {

        User loginUser = userService.login(request.getEmail(), request.getPassword());

        if (loginUser == null) {
            return ResponseEntity
                    .badRequest()
                    .body("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        return ResponseEntity.ok(new LoginResponse(loginUser.getId(), loginUser.getEmail()));
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return "redirect:/";
    }
}
