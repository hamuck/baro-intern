package com.example.barointern.global.jwt;

import static com.example.barointern.global.constans.Constans.AUTHORIZATION;
import static com.example.barointern.global.constans.Constans.BEARER;
import static com.example.barointern.global.constans.Constans.REFRESH_TOKEN;
import static com.example.barointern.global.constans.Constans.REFRESH_TOKEN_EXPIRATION;


import com.example.barointern.domain.user.User;
import com.example.barointern.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;

	public JwtFilter(JwtProvider jwtProvider, UserRepository userRepository){
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain) throws ServletException, IOException {

		String requestPath = request.getRequestURI();
		String method = request.getMethod();

		// Access Token과 Refresh Token을 가져오기
		String accessToken = request.getHeader(AUTHORIZATION);
		if (accessToken != null && accessToken.startsWith(BEARER)) {
			accessToken = accessToken.substring(BEARER.length());
		}
		String refreshToken = getCookie(request, REFRESH_TOKEN);

		try {
			if (accessToken != null) {

				// Access Token이 유효한 경우, 인증 정보 설정
				if (jwtProvider.validationAccessToken(accessToken)) {
					setAuthentication(accessToken, request);
				}

				// Access Token이 만료되었고 Refresh Token이 유효한 경우, 새로운 Access Token 발급
				else if (refreshToken != null && jwtProvider.validationRefreshToken(refreshToken)) {
					String userId = jwtProvider.extractUserIdFromRefresh(refreshToken);

					User user = userRepository.findByIdOrElseThrow(Long.valueOf(userId));

					// 새 Access Token 발급
					String newAccessToken = jwtProvider.regenerateAccessToken(refreshToken, user.getEmail());

					// 응답 헤더에 새 Access Token 설정
					response.setHeader(AUTHORIZATION, BEARER + newAccessToken);

					// 새 Access Token을 사용하여 인증 정보 설정
					setAuthentication(newAccessToken, request);

					// 새로운 Refresh Token 발급 및 설정
					String newRefreshToken = jwtProvider.generateRefreshToken(Long.valueOf(userId));
					response.addCookie(
						jwtProvider.createHttpOnlyCookie(REFRESH_TOKEN, newRefreshToken,
							REFRESH_TOKEN_EXPIRATION));
				}
			}
		} catch (Exception e) {
			log.error("Cannot set user authentication: {}", e.getMessage());
		}
		chain.doFilter(request, response);
	}

	private void setAuthentication(String token, HttpServletRequest request) {
		String email = jwtProvider.extractEmail(token);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(email, null, null);

		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);

		log.debug("Authentication set for user: {}", email);
	}

	private String getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookieName.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
