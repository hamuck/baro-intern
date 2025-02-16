package com.example.barointern.domain.auth.controller;

import com.example.barointern.domain.auth.dto.LoginRequestDto;
import com.example.barointern.domain.auth.dto.LoginResponseDto;
import com.example.barointern.domain.auth.dto.SignupRequestDto;
import com.example.barointern.domain.auth.dto.SignupResponseDto;
import com.example.barointern.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/signup")
	public ResponseEntity<SignupResponseDto> signup(
		@RequestBody SignupRequestDto signupRequestDto) {
		SignupResponseDto responseDto = authService.signup(signupRequestDto);

		return new ResponseEntity<SignupResponseDto>(responseDto, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponseDto> login(
		@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
		LoginResponseDto responseDto = authService.login(loginRequestDto, response);

		return new ResponseEntity<LoginResponseDto>(responseDto, HttpStatus.OK);
	}
}
