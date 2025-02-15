package com.example.barointern.global.constans;

public interface Constans {
	//헤더 상수
	String AUTHORIZATION = "Authorization";
	String BEARER = "Bearer ";
	int ACCESS_TOKEN_EXPIRATION = (int) (15 * 60 * 1000L); // 15분
	int REFRESH_TOKEN_EXPIRATION = (int) (7 * 24 * 60 * 60 * 1000L); // 7일
	String ACCESS_TOKEN = "access_token";
	String REFRESH_TOKEN = "refresh_token";
}
