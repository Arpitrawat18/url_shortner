package com.Project.URL_Shortner.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI urlShortenerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("URL Shortener API")
                        .version("1.0")
                        .description("REST API for URL Shortener with JWT Authentication, Redis Caching and Analytics")
                        .contact(new Contact()
                                .name("Arpit Rawat")
                                .email("arpitrawat.dev@gmail.com")));
    }
}