package com.clone.Airbnb.jwt.web.user.signup;

import com.clone.Airbnb.web.user.dto.SignupRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 회원가입_성공() throws Exception {
        // given
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("테스트0");
        signupRequest.setEmail("test0@example.com");
        signupRequest.setPassword("Password123!");
        signupRequest.setConfirmPassword("Password123!");

        String jsonRequest = objectMapper.writeValueAsString(signupRequest);

        // when & then
        mockMvc.perform(post("/api/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated());
    }

    @Test
    void 이미_가입된_이메일로_회원가입_실패() throws Exception {
        // given: 먼저 회원가입을 성공시켜 중복 이메일을 만듭니다.
        SignupRequest firstSignupRequest = new SignupRequest();
        firstSignupRequest.setName("테스트1");
        firstSignupRequest.setEmail("duplicate@example.com");
        firstSignupRequest.setPassword("Password123!");
        firstSignupRequest.setConfirmPassword("Password123!");

        mockMvc.perform(post("/api/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstSignupRequest)))
                .andExpect(status().isCreated());

        // given: 동일한 이메일로 두 번째 회원가입 요청을 준비합니다.
        SignupRequest secondSignupRequest = new SignupRequest();
        secondSignupRequest.setName("테스트2");
        secondSignupRequest.setEmail("duplicate@example.com"); // 중복 이메일
        secondSignupRequest.setPassword("Password123!");
        secondSignupRequest.setConfirmPassword("Password123!");

        String jsonRequest = objectMapper.writeValueAsString(secondSignupRequest);

        // when & then: 두 번째 요청은 실패해야 합니다.
        mockMvc.perform(post("/api/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists()); // 에러 메시지가 존재하는지 확인
    }
}