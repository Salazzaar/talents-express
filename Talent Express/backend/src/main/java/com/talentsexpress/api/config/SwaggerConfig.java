package com.talentsexpress.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger UI / OpenAPI 3.
 *
 * Acesse em: http://localhost:8080/swagger-ui.html
 *
 * Como usar o token JWT no Swagger:
 *   1. Execute POST /api/auth/login ou /api/auth/register
 *   2. Copie o valor do campo "token" na resposta
 *   3. Clique no botão "Authorize 🔓" no topo da página
 *   4. Cole o token no campo "Value" (sem "Bearer ", só o token)
 *   5. Clique em "Authorize" — todos os endpoints protegidos passam a funcionar
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Talents Express API")
                        .description("Plataforma de serviços sob demanda — Barueri/SP\n\n" +
                                "**Como autenticar:**\n" +
                                "1. Chame `POST /api/auth/register` ou `POST /api/auth/login`\n" +
                                "2. Copie o campo `token` da resposta\n" +
                                "3. Clique em **Authorize 🔓** acima e cole o token\n" +
                                "4. Pronto! Todos os endpoints protegidos agora funcionam.")
                        .version("1.0.0-MVP")
                        .contact(new Contact()
                                .name("Talents Express")
                                .email("contato@talentsexpress.com.br")))
                // Define o esquema de segurança Bearer JWT
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .name("bearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Cole aqui o token JWT recebido no login (sem o prefixo 'Bearer ')")));
    }
}
