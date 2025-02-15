package com.example.barointern.global.config;

import com.example.barointern.global.jwt.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	public SecurityConfig(JwtFilter jwtFilter){
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/login", "/signup").permitAll()
				.anyRequest().authenticated()) // 그 외 모든 요청은 인증 필요
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT를 사용하므로 세션 비활성화
			.formLogin(form -> form.disable()) // 기본 폼 로그인 비활성화
			.httpBasic(httpBasic -> httpBasic.disable()) // HTTP Basic 인증 비활성화
			.addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class); // JWT 필터를 인증 필터 앞에 추가

		return http.build();
	}
}
