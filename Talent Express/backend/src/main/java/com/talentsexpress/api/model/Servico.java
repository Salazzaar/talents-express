package com.talentsexpress.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço ofertado por um Prestador.
 * Núcleo do portfólio.
 * RN02: campo 'ativo' controla visibilidade nas buscas.
 */
@Entity
@Table(name = "servicos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id", nullable = false)
    private Usuario prestador;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Categoria categoria;

    @Column(name = "preco_hora", nullable = false)
    private BigDecimal precoHora;

    /** RN02: true = visível nas buscas; false = oculto */
    @Builder.Default
    private boolean ativo = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "servico_dias", joinColumns = @JoinColumn(name = "servico_id"))
    @Column(name = "dia")
    private List<String> diasDisponiveis;

    @Column(name = "hora_inicio")
    private String horaInicio;

    @Column(name = "hora_fim")
    private String horaFim;

    @Builder.Default
    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    /**
     * Categorias de serviço.
     * Sem acentos nos nomes do enum para compatibilidade com valueOf() e JPA EnumType.STRING.
     */
    public enum Categoria {
        BRACAL("Braçal"),
        INTELECTUAL("Intelectual"),
        ARTISTICO("Artístico");

        private final String descricao;
        Categoria(String descricao) { this.descricao = descricao; }
        public String getDescricao() { return descricao; }
    }
}
