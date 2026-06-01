package com.talentsexpress.api.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Stub de Message Broker (RabbitMQ/Kafka)
 *
 * Design de Retaguarda: em produção, este método publicaria uma mensagem
 * na fila "pagamentos.processar" do RabbitMQ ou no tópico Kafka equivalente.
 *
 * Para o MVP acadêmico, simula o processamento assíncrono com um delay
 * e log de confirmação — representando o padrão Produtor/Consumidor.
 *
 * Integração futura:
 *   @RabbitListener(queues = "pagamentos.processar") no consumer
 *   rabbitTemplate.convertAndSend("pagamentos.processar", payload) aqui
 */
@Service
@Slf4j
public class PagamentoMessageBrokerStub {

    /**
     * Publica (simulado) a intenção de pagamento na fila assíncrona.
     * @Async garante execução em thread separada — não bloqueia a resposta HTTP.
     */
    @Async("pagamentoExecutor")
    public void publicarPagamento(Long solicitacaoId, BigDecimal valor, String metodo) {
        String correlationId = UUID.randomUUID().toString();
        log.info("[BROKER-STUB] ▶ Publicando pagamento na fila: solicitacaoId={}, valor=R${}, metodo={}, correlationId={}",
                solicitacaoId, valor, metodo, correlationId);

        try {
            // Simula latência de rede/processamento (200-800ms)
            Thread.sleep(500);

            // Simula processamento do consumer
            log.info("[BROKER-STUB] ✅ Pagamento processado: correlationId={}, timestamp={}",
                    correlationId, LocalDateTime.now());

        } catch (InterruptedException e) {
            log.error("[BROKER-STUB] ❌ Erro no processamento: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Tópico/Fila que seria usado em produção.
     * Ex: "talents.pagamentos.processar" no RabbitMQ
     *     "pagamentos-topic" no Kafka
     */
    public static final String FILA_PAGAMENTOS = "talents.pagamentos.processar";
}
