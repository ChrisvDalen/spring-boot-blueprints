package com.example.openapi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Centralised OpenAPI metadata configuration.
 *
 * Why a config bean instead of application.yml properties?
 * The Java API lets you compose complex schemas (security schemes,
 * server lists, extensions) that YAML properties can't express.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Galactic Product Catalogue API")
                        .description("""
                                The official catalogue of the Galactic Republic's product offerings.

                                This API demonstrates:
                                - Proper OpenAPI annotation coverage
                                - Security scheme declaration
                                - Schema descriptions that explain domain concepts
                                - Response code documentation for both success and error paths
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Galactic Engineering Team")
                                .email("engineering@galactic-republic.example.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT bearer token — obtain via POST /auth/login")));
    }
}
