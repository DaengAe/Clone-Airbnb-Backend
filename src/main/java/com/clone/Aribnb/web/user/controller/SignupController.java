package com.clone.Aribnb.web.user.controller;

import com.clone.Aribnb.domain.user.User;
import com.clone.Aribnb.domain.user.UserRole;
import com.clone.Aribnb.domain.user.repository.UserRepository;
import com.clone.Aribnb.domain.user.service.UserService;
import com.clone.Aribnb.web.user.dto.SignupRequest;
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
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupForm,
                                    BindingResult bindingResult) {

        Map<String, String> errors = new HashMap<>();

        // 기본 폼 유효성 검사 실패 시 에러 리턴
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        // 비밀번호 확인 일치 여부 검사
        if (!signupForm.passwordsMatch()) {
            errors.put("confirmPassword", "비밀번호가 일치하지 않습니다.");
            return ResponseEntity.badRequest().body(errors);
        }

        // 이메일 중복 검사
        if (userRepository.findByEmail(signupForm.getEmail()).isPresent()) {
            errors.put("email", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.badRequest().body(errors);
        }

        // 회원 생성 및 저장
        User user = User.builder()
                .name(signupForm.getName())
                .email(signupForm.getEmail())
                // 비밀번호 암호화 후 저장 (userService 내부에서 처리하거나 아래와 같이 직접 처리 가능)
                // 예시: passwordEncoder.encode(signupForm.getPassword())
                .password(userService.encodePassword(signupForm.getPassword()))
                .role(UserRole.USER) // 기본 권한 USER 부여
                .build();

        userRepository.save(user);

        // 성공 시 메시지 반환
        Map<String, String> success = new HashMap<>();
        success.put("message", "회원가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(success);
    }
}

