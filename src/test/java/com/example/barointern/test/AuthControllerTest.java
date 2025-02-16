//package com.example.barointern.test;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//public class AuthControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@BeforeEach
//	public void setUp() throws Exception {
//		// 여기서 로그인에 필요한 초기 데이터를 세팅할 수 있습니다.
//	}
//
//	@AfterEach
//	public void tearDown() throws Exception {
//		// 각 테스트 후에 필요한 정리 작업을 여기에 추가할 수 있습니다.
//	}
//
//	@Test
//	public void testSignup() throws Exception {
//		String signupRequestJson = """
//           {
//               "username": "JIN HO",
//               "password": "12341234",
//               "nickname": "Mentos"
//           }
//           """;
//
//		mockMvc.perform(post("/signup")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(signupRequestJson))
//			.andExpect(status().isCreated()) // 성공적으로 생성되었을 경우 201 응답
//			.andExpect(jsonPath("$.username").value("JIN HO")) // 반환된 JSON의 username 확인
//			.andExpect(jsonPath("$.nickname").value("Mentos")) // 반환된 JSON의 nickname 확인
//			.andDo(MockMvcResultHandlers.print()); // 응답 내용 출력
//	}
//
//	@Test
//	public void testLogin() throws Exception {
//		// 1. 먼저 회원가입
//		String signupRequestJson = """
//           {
//               "username": "JIN HO",
//               "password": "12341234",
//               "nickname": "Mentos"
//           }
//           """;
//
//		mockMvc.perform(post("/signup")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(signupRequestJson))
//			.andExpect(status().isCreated()) // 회원가입이 성공적으로 완료되면 201
//			.andDo(MockMvcResultHandlers.print());
//
//		// 2. 그 다음 로그인
//		String loginRequestJson = """
//           {
//               "username": "JIN HO",
//               "password": "12341234"
//           }
//           """;
//
//		mockMvc.perform(post("/login")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(loginRequestJson))
//			.andExpect(status().isOk()) // 로그인 성공 시 200 응답
//			.andExpect(jsonPath("$.token").exists()) // 응답에서 token이 있는지 확인
//			.andDo(MockMvcResultHandlers.print()); // 응답 내용 출력
//	}
//
//	@Test
//	public void testLoginWithInvalidCredentials() throws Exception {
//		String loginRequestJson = """
//           {
//               "username": "wrong_user",
//               "password": "wrong_password"
//           }
//           """;
//
//		mockMvc.perform(post("/login")
//				.contentType(MediaType.APPLICATION_JSON)
//				.content(loginRequestJson))
//			.andExpect(status().is4xxClientError()) // 잘못된 자격 증명일 경우 401 응답
//			.andDo(MockMvcResultHandlers.print()); // 응답 내용 출력
//	}
//}
