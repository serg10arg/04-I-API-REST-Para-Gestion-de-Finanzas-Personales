package com.example.finanzaspersonales.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI (Swagger) para la documentación de la API.
 * Esto permite visualizar y probar los endpoints de la API en /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Define el bean OpenAPI para configurar la información de la API y la seguridad.
     * @return Una instancia de OpenAPI con la configuración.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                )
                .info(new Info()
                        .title("API REST para Gestión de Finanzas Personales")
                        .version("1.0")
                        .description("Backend para una aplicación de finanzas personales, permitiendo el registro de ingresos y gastos, su categorización y la generación de reportes." +
                                "Se utiliza JWT para la autenticación y Spring Security para proteger los endpoints.")
                );
    }
}

