package app.internos.servicea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ServiceAIdentityApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ServiceAIdentityApplication.class, args);
    }
}

