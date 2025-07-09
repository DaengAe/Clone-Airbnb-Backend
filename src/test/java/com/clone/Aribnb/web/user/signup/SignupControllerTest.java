package com.clone.Aribnb.web.user.signup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser
        // 인증된 사용자로 테스트 실행
    void 회원가입_성공() throws Exception {
        mockMvc.perform(post("/signup")
                        .with(csrf())  // 여기서 CSRF 토큰 추가
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "홍길동")
                        .param("email", "test@example.com")
                        .param("password", "Password1!")
                        .param("confirmPassword", "Password1!"))
                .andExpect(status().is3xxRedirection()); // 성공하면 리다이렉트 기대
    }
}