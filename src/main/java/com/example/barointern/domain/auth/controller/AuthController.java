package com.example.barointern.domain.auth.controller;

import com.example.barointern.domain.auth.dto.LoginRequestDto;
import com.example.barointern.domain.auth.dto.LoginResponseDto;
import com.example.barointern.domain.auth.dto.SignupRequestDto;
import com.example.barointern.domain.auth.dto.SignupResponseDto;
import com.example.barointern.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "회원가입 및 로그인 관련 API")
@RestController
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@Operation(summary = "회원가입", description = "새로운 사용자를 회원가입시킵니다.")
	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(
		@RequestBody SignupRequestDto signupRequestDto) {
		SignupResponseDto responseDto = authService.signup(signupRequestDto);

		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	@Operation(summary = "로그인", description = "기존 사용자가 로그인합니다. 로그인 성공 시 JWT를 반환합니다.")
	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(
		@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
		LoginResponseDto responseDto = authService.login(loginRequestDto, response);

		return new ResponseEntity<>(responseDto, HttpStatus.OK);
	}
}
