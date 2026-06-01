package com.talentsexpress.api.service;

import com.talentsexpress.api.dto.IniciarPagamentoDTO;
import com.talentsexpress.api.messaging.PagamentoMessageBrokerStub;
import com.talentsexpress.api.model.Solicitacao;
import com.talentsexpress.api.repository.SolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagamentoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final PagamentoMessageBrokerStub broker;

    /**
     * Registra intenção de pagamento e envia para fila assíncrona.
     * Simula o padrão Message Broker (RabbitMQ/Kafka) para fins acadêmicos.
     */
    public Map<String, Object> iniciar(IniciarPagamentoDTO dto) {
        Solicitacao sol = solicitacaoRepository.findById(dto.solicitacaoId())
                .orElseThrow(() -> new BusinessException("Solicitação #" + dto.solicitacaoId() + " não encontrada."));

        if (sol.getStatus() != Solicitacao.Status.AGUARDANDO_PAGAMENTO) {
            throw new BusinessException("Esta solicitação não está aguardando pagamento.");
        }

        // Publica na fila assíncrona (não bloqueia resposta HTTP)
        broker.publicarPagamento(sol.getId(), dto.valor(), dto.metodo());

        // Simulação do MVP: Pagamento aprovado automaticamente para fechar o fluxo
        sol.setStatus(Solicitacao.Status.CONCLUIDO);
        solicitacaoRepository.save(sol);

        String correlationId = UUID.randomUUID().toString();
        return Map.of(
                "correlationId", correlationId,
                "status", "CONFIRMADO",
                "mensagem", "Pagamento aprovado com sucesso! O serviço foi concluído.",
                "solicitacaoId", sol.getId(),
                "valor", dto.valor(),
                "metodo", dto.metodo()
        );
    }

    public Map<String, Object> consultarStatus(String correlationId) {
        // Stub: em produção consultaria o banco ou o broker
        return Map.of(
                "correlationId", correlationId,
                "status", "CONFIRMADO",
                "mensagem", "Pagamento processado com sucesso. (simulação)"
        );
    }
}
