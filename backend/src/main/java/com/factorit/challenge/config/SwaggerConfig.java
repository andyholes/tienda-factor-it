package com.factorit.challenge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl("http://localhost:8080");
        devServer.setDescription("Server URL in Development environment");

        Server deployment = new Server();
        deployment.setUrl("https://tienda-factorit-2.onrender.com");
        deployment.setDescription("Deployment URL");

        Contact contact = new Contact();
        contact.setEmail("andyholes@gmail.com");
        contact.setName("Andres Hoyos Garcia");
        contact.setUrl("https://andyholes.xyz/");

        Info info = new Info()
                .title("Tienda FACTOR IT - Challenge de Andres Hoyos Garcia")
                .version("1.0")
                .contact(contact)
                .description("Api desarrollada para el challenge fullstack de Factor IT. Se uso con Java y Spring Boot, con base de datos en memoria H2.");

        return new OpenAPI().info(info).servers(List.of(deployment, devServer));
    }
}
