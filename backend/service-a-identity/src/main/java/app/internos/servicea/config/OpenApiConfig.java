package app.internos.servicea.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Inter Nos API - Service A (Identity & Portal)")
                        .version("1.0.0")
                        .description("Service A: Identity & Portal - User authentication, session management, dashboard"))
                .servers(List.of(
                        new Server()
                                .url("https://api.internos.app/a/v1")
                                .description("Production Server"),
                        new Server()
                                .url("http://localhost:8080/a/v1")
                                .description("Local Development Server")
                ));
    }
}

