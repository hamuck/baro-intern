package com.example.barointern.domain.auth.service;

import static com.example.barointern.global.constans.Constans.REFRESH_TOKEN;
import static com.example.barointern.global.constans.Constans.REFRESH_TOKEN_EXPIRATION;

import com.example.barointern.domain.auth.dto.LoginRequestDto;
import com.example.barointern.domain.auth.dto.LoginResponseDto;
import com.example.barointern.domain.auth.dto.SignupRequestDto;
import com.example.barointern.domain.auth.dto.SignupResponseDto;
import com.example.barointern.domain.user.User;
import com.example.barointern.domain.user.UserRepository;
import com.example.barointern.global.enums.UserRole;
import com.example.barointern.global.jwt.JwtProvider;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtProvider jwtProvider;

	public AuthService(PasswordEncoder passwordEncoder, UserRepository userRepository,AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
		this.passwordEncoder = passwordEncoder;
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.jwtProvider = jwtProvider;
	}

	public SignupResponseDto signup(SignupRequestDto dto) {
		String encodedPassword = passwordEncoder.encode(dto.getPassword());

		if (userRepository.existsByUsername(dto.getUsername())){
			throw new RuntimeException("중복된 아이디입니다.");
		}

		User user = new User(dto.getUsername(),encodedPassword,dto.getNickname());

		userRepository.save(user);

		SignupResponseDto responseDto = new SignupResponseDto(dto.getUsername(),dto.getNickname(),
			Collections.singletonList("ROLE_" + UserRole.USER.toString()));

		return responseDto;
	}

	public LoginResponseDto login(LoginRequestDto dto, HttpServletResponse response){
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

		User user = userRepository.findByUsernameOrElseThrow(dto.getUsername());

		//기존 리프레시 토큰 삭제
		response.addCookie(jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN,"",0));

		// 로그인 성공 시 토큰 생성 후 반환
		String accessToken = jwtProvider.generateAccessToken(user.getUsername(), user.getId());
		String refreshToken = jwtProvider.generateRefreshToken(user.getId());

		// 리프레시 토큰을 HTTP-Only 쿠키에 저장
		response.addCookie(jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN, refreshToken,
			REFRESH_TOKEN_EXPIRATION));

		return new LoginResponseDto(accessToken);
	}
}
