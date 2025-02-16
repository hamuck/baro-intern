package com.example.barointern.global.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("바로인턴 java")
				.description("API 설명")
				.version("1.0.0")
				.termsOfService("http://swagger.io/terms/")
				.contact(new Contact().name("your-name").email("your-email"))
			)
			.externalDocs(new ExternalDocumentation()
				.description("추가 문서")
				.url("http://swagger.io/docs"));
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("public")
			.pathsToMatch("/**")
			.build();
	}
}