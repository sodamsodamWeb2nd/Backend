package com.example.sodamsodam.apps.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "SodamSodam API",
                version = "v1",
                description = "SodamSodam 서비스의 RESTful API 문서입니다.",
                contact = @Contact(
                        name = "SodamSodam Team",
                        email = "contact@sodamsodam.com",
                        url = "https://www.sodamsodam.com"
                ),
                license = @License(
                        name = "Apache 2.0",
                        url = "https://www.apache.org/licenses/LICENSE-2.0"
                )
        ),
        servers = {
                @Server(url = "/", description = "Local Development Server")
        },
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "JWT 인증을 위한 Bearer 토큰을 입력하세요. 로그인 API를 통해 토큰을 발급받을 수 있습니다."
)
@Configuration
public class SwaggerConfig {
}