package com.example.barointern.global.jwt;

import static com.example.barointern.global.constans.Constans.*;

import com.example.barointern.domain.user.User;
import com.example.barointern.domain.user.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtProvider jwtProvider;
	private final UserRepository userRepository;

	// JWT 필터가 적용되지 않을 API 화이트리스트
	private static final List<String> WHITE_LIST = List.of(
		"/swagger-ui/", "/swagger-ui.html", "/swagger-ui/index.html",
		"/v3/api-docs/", "/v3/api-docs.yaml", "/swagger-resources/", "/v3/api-docs",
		"/v3/api-docs/swagger-config",
		"/login", "/signup","/v3/api-docs/swagger-config"
	);


	public JwtFilter(JwtProvider jwtProvider, UserRepository userRepository) {
		this.jwtProvider = jwtProvider;
		this.userRepository = userRepository;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		boolean shouldSkip = WHITE_LIST.stream().anyMatch(path::startsWith);

		log.debug("Request URI: {}, Should Skip JWT Filter: {}", path, shouldSkip);
		return shouldSkip;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain chain) throws ServletException, IOException {

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
					String newAccessToken = jwtProvider.regenerateAccessToken(refreshToken, user.getUsername());

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
		String username = jwtProvider.extractUsername(token);
		User user = userRepository.findByUsernameOrElseThrow(username);

		if (user.getUserRole() != null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				username, null, List.of(new SimpleGrantedAuthority(user.getUserRole().toString())));
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.debug("Authentication set for user: {}", username);
		}
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
