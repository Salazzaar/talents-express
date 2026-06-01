package com.talentsexpress.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuração do executor assíncrono para processamento de pagamentos.
 * Referenciado em PagamentoMessageBrokerStub com @Async("pagamentoExecutor").
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "pagamentoExecutor")
    public Executor pagamentoExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("pagamento-async-");
        executor.initialize();
        return executor;
    }
}
