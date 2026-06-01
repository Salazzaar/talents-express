package com.talentsexpress.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Talents Express — Módulo de Serviço (Backend)
 * Plataforma de serviços sob demanda — Barueri/SP
 *
 * Arquitetura em Camadas (MVC):
 *   ┌─────────────────────────────────────────┐
 *   │  API/Controller  (REST endpoints)       │
 *   ├─────────────────────────────────────────┤
 *   │  Service/Negócio (regras RN01-RF07)     │
 *   ├─────────────────────────────────────────┤
 *   │  Repository/Dados (JPA + H2/PostgreSQL) │
 *   └─────────────────────────────────────────┘
 *
 * Async habilitado via AsyncConfig (@EnableAsync está lá)
 */
@SpringBootApplication
public class TalentsExpressApplication {
    public static void main(String[] args) {
        SpringApplication.run(TalentsExpressApplication.class, args);
    }
}
