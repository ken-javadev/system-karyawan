package com.system.karyawan.swagger;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class SwaggerProperties {

    @Bean
    public OpenAPI springDoc() {
        return new OpenAPI()
                .info(new Info()
                        .title("spring-boot-jwt")
                        .version("v1"))
                .externalDocs(new ExternalDocumentation()
                        .description("XL Knowledge Management")
                        .url("https://confluence.xl.co.id"));
    }
}
