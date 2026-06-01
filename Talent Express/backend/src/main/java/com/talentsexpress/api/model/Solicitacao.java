package com.talentsexpress.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Solicitação de serviço criada por um Cliente.
 * Ciclo: PENDENTE → ACEITO → CONCLUIDO / RECUSADO / CANCELADO
 */
@Entity
@Table(name = "solicitacoes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Usuario prestador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id")
    private Servico servico;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_hora_agendada", nullable = false)
    private LocalDateTime dataHoraAgendada;

    @Column(nullable = false)
    private String endereco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDENTE;

    private BigDecimal valor;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    @Column(name = "aceito_em")
    private LocalDateTime aceitoEm;

    // RF07: versão para controle de concorrência otimista
    @Version
    private Long version;

    public enum Status { PENDENTE, ACEITO, RECUSADO, CANCELADO, CONCLUIDO, AGUARDANDO_PAGAMENTO }
}
