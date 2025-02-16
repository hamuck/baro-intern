package com.example.barointern.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginResponseDto {
	private String token;

	public LoginResponseDto(){}
	public LoginResponseDto(String token){
		this.token = token;
	}

}
