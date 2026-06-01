package com.talentsexpress.api.service;

import com.talentsexpress.api.model.*;
import com.talentsexpress.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Camada de Negócio — Motor de Matching
 * Aplica:
 *   RN02 — Só exibe prestadores com perfil completo e serviço ativo
 *   RNF01 — Processamento estruturado para < 3 segundos
 *
 * Algoritmo de Scoring:
 *   Score = (avaliação × 0.5) + (proximidade × 0.3) + (disponibilidade × 0.2)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingEngine {

    private final ServicoRepository servicoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    /**
     * Busca e ranqueia prestadores elegíveis.
     * RNF01: resultado deve ser retornado em < 3 segundos.
     */
    public List<MatchResultDTO> buscar(String texto, String categoria, String bairro) {
        long inicio = System.currentTimeMillis();

        // RN02: apenas serviços ativos de prestadores com perfil completo
        List<Servico> servicos = servicoRepository.findElegiveis(texto, categoria, bairro);

        List<MatchResultDTO> resultados = servicos.stream()
                .map(s -> {
                    double mediaAvaliacao = avaliacaoRepository
                            .findMediaByAvaliado(s.getPrestador().getId())
                            .orElse(0.0);
                    double score = calcularScore(mediaAvaliacao, s.getPrestador().getBairro(), bairro);
                    return new MatchResultDTO(
                            s.getId(),
                            s.getPrestador().getId(),
                            s.getPrestador().getNome(),
                            s.getTitulo(),
                            s.getCategoria().getDescricao(),
                            s.getPrecoHora(),
                            mediaAvaliacao,
                            (int) avaliacaoRepository.countByAvaliadoId(s.getPrestador().getId()),
                            s.getPrestador().getBairro(),
                            true,
                            s.getDescricao(),
                            score
                    );
                })
                .sorted(Comparator.comparingDouble(MatchResultDTO::score).reversed())
                .collect(Collectors.toList());

        long elapsed = System.currentTimeMillis() - inicio;
        log.info("MatchingEngine: {} resultado(s) em {}ms (RNF01: <3000ms)", resultados.size(), elapsed);

        return resultados;
    }

    private double calcularScore(double avaliacao, String bairroPrestador, String bairroFiltro) {
        double scoreAvaliacao = (avaliacao / 5.0) * 50;
        double scoreProximidade = (bairroFiltro != null && bairroFiltro.equalsIgnoreCase(bairroPrestador)) ? 30 : 15;
        double scoreDisponibilidade = 20; // placeholder — integrável com agenda futura
        return scoreAvaliacao + scoreProximidade + scoreDisponibilidade;
    }

    public record MatchResultDTO(
            Long servicoId, Long prestadorId, String nome, String servico,
            String categoria, java.math.BigDecimal preco,
            double avaliacao, int avaliacoes, String bairro,
            boolean disponivel, String descricao, double score) {}
}
