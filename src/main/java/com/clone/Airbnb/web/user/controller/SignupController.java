package com.clone.Airbnb.web.user.controller;

import com.clone.Airbnb.domain.user.service.UserService;
import com.clone.Airbnb.web.user.dto.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SignupController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrors(bindingResult));
        }

        userService.signup(signupRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입 성공"));
    }

    private Map<String, String> getErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 서비스 계층에서 발생한 예외(예: 이메일 중복)를 처리
        // 실제 필드 특정 에러라면 더 정교한 처리가 필요할 수 있음
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}

