package net.gentledot.demorestapi.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi eventOpenApi() {
        String[] paths = {"/api/events/**"};

        return GroupedOpenApi.builder()
                .setGroup("events")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi indexOpenApi() {
        String[] packages = {"net.gentledot.demorestapi"};

        return GroupedOpenApi.builder()
                .setGroup("all")
                .packagesToScan(packages)
                .build();
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicScheme",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"))
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info().title("Event REST API").description("demo REST API이고 GET요청 외에는 인증된 사용자면 요청 가능."));
    }

}
