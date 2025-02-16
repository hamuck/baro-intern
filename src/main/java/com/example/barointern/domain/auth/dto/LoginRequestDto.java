package com.example.barointern.domain.auth.dto;

import lombok.Getter;

@Getter
public class LoginRequestDto {
	private String username;
	private String password;

	public LoginRequestDto(){}

	public LoginRequestDto(String username, String password){
		this.username = username;
		this.password = password;
	}

}
