package com.gkfcsolution.taskmanager.web.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.application.name:Task Manager API}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("""
                                API de gestion de tâches permettant :
                                - Gestion des utilisateurs (authentification, autorisation)
                                - Gestion des tâches (CRUD, assignation, statut)
                                - Gestion des projets
                                - Gestion des commentaires
                                - Notifications en temps réel
                                """)
                        .version(applicationVersion)
                        .contact(new Contact()
                                .name("Frank GUEKENG")
                                .email("frank@example.com")
                                .url("https://github.com/frank-guekeng"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.taskmanager.com")
                                .description("Serveur de production")
                ))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("""
                                        Entrez votre token JWT dans le format :
                                        Bearer {votre_token}
                                        """)
                                .name("Authorization")
                                .in(SecurityScheme.In.HEADER)));
    }
}