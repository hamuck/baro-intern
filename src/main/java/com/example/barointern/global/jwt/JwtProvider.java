package com.example.barointern.global.jwt;

import static com.example.barointern.global.constans.Constans.ACCESS_TOKEN_EXPIRATION;
import static com.example.barointern.global.constans.Constans.BEARER;
import static com.example.barointern.global.constans.Constans.REFRESH_TOKEN_EXPIRATION;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {
	SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	public String generateAccessToken(String username, Long userId) {
		return Jwts.builder()
			.setSubject(username)
			.claim("userId", userId)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
			.signWith(secretKey)
			.compact();
	}

	public String generateRefreshToken(Long userId) {
		return Jwts.builder()
			.setSubject(userId.toString())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
			.signWith(secretKey)
			.compact();
	}

	public String extractBearer(String token) {
		if (token != null && token.startsWith(BEARER)) {
			return token.substring(BEARER.length()).trim();
		}
		return token;
	}

	public Claims extractClaims(String token) {
		token = extractBearer(token);
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token)
				.getBody();
		} catch (JwtException e) {
			throw new RuntimeException("토큰이 만료되었습니다.");
		}
	}

	public String extractUserIdFromRefresh(String token) {
		return extractClaims(token).getSubject();
	}

	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	public boolean validationAccessToken(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build()
			.parseClaimsJws(token)
			.getBody();
		return !claims.getExpiration().before(new Date());
	}

	public boolean validationRefreshToken(String token) {
		Claims claims = extractClaims(token);
		return !claims.getExpiration().before(new Date());
	}

	public String regenerateAccessToken(String refreshToken, String username) {
		if (!validationRefreshToken(refreshToken)) {
			throw new RuntimeException("토큰이 만료되었습니다.");
		}
		Claims claims = extractClaims(refreshToken);
		Long userId = Long.parseLong(claims.getSubject());
		return generateAccessToken(username, userId);
	}

	public Cookie createHttpOnlyCookie(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setAttribute("SameSite", "None");
		cookie.setMaxAge(maxAge);
		return cookie;
	}
}
