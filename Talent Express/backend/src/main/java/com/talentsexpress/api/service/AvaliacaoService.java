package com.talentsexpress.api.service;

import com.talentsexpress.api.dto.CriarAvaliacaoDTO;
import com.talentsexpress.api.model.Avaliacao;
import com.talentsexpress.api.model.Solicitacao;
import com.talentsexpress.api.model.Usuario;
import com.talentsexpress.api.repository.AvaliacaoRepository;
import com.talentsexpress.api.repository.SolicitacaoRepository;
import com.talentsexpress.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Avaliacao criar(CriarAvaliacaoDTO dto, Long avaliadorId) {
        Solicitacao sol = solicitacaoRepository.findById(dto.solicitacaoId())
                .orElseThrow(() -> new BusinessException("Solicitação #" + dto.solicitacaoId() + " não encontrada."));

        if (sol.getStatus() != Solicitacao.Status.CONCLUIDO) {
            throw new BusinessException("Só é possível avaliar serviços concluídos.");
        }

        Usuario avaliador = usuarioRepository.findById(avaliadorId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        // Impede dupla avaliação
        if (avaliacaoRepository.findBySolicitacaoId(sol.getId()).isPresent()) {
            throw new BusinessException("Esta solicitação já foi avaliada.");
        }

        Avaliacao avaliacao = Avaliacao.builder()
                .solicitacao(sol)
                .avaliador(avaliador)
                .avaliado(sol.getPrestador())
                .nota(dto.nota())
                .comentario(dto.comentario())
                .build();

        return avaliacaoRepository.save(avaliacao);
    }

    public List<Avaliacao> listarPorPrestador(Long prestadorId) {
        return avaliacaoRepository.findByAvaliadoId(prestadorId);
    }

    public Map<String, Object> mediaEContagem(Long prestadorId) {
        double media = avaliacaoRepository.findMediaByAvaliado(prestadorId).orElse(0.0);
        long total = avaliacaoRepository.countByAvaliadoId(prestadorId);
        return Map.of("media", Math.round(media * 10.0) / 10.0, "total", total);
    }
}
