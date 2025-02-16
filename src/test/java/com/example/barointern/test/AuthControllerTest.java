package com.example.barointern.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testSignup() throws Exception {
		String signupRequestJson = """
           {
               "username": "JIN HO",
               "password": "12341234",
               "nickname": "Mentos"
           }
           """;

		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(signupRequestJson))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.username").value("JIN HO"))
			.andExpect(jsonPath("$.nickname").value("Mentos"))
			.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testLogin() throws Exception {
		// 1. 먼저 회원가입
		String signupRequestJson = """
           {
               "username": "JIN HO",
               "password": "12341234",
               "nickname": "Mentos"
           }
           """;

		mockMvc.perform(post("/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(signupRequestJson))
			.andExpect(status().isCreated())
			.andDo(MockMvcResultHandlers.print());

		// 2. 그 다음 로그인
		String loginRequestJson = """
           {
               "username": "JIN HO",
               "password": "12341234"
           }
           """;

		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequestJson))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists())
			.andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void testLoginWithInvalidCredentials() throws Exception {
		String loginRequestJson = """
           {
               "username": "wrong_user",
               "password": "wrong_password"
           }
           """;

		mockMvc.perform(post("/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(loginRequestJson))
			.andExpect(status().isUnauthorized())
			.andDo(MockMvcResultHandlers.print());
	}
}