package com.mito.graphms.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Mito Graph Microservice API", 
        version = "1.0.0",
        description = "API per la gestione di nodi e relazioni in un database Neo4j",
        contact = @Contact(
            name = "Team MITO", 
            email = "support@mito.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080", description = "Server di sviluppo locale")
    }
)
public class OpenApiConfig {
    // Configurazione base per OpenAPI
}
