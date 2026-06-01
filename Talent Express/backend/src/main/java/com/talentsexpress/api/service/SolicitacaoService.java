package com.talentsexpress.api.service;

import com.talentsexpress.api.dto.*;
import com.talentsexpress.api.model.*;
import com.talentsexpress.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Camada de Negócio — Solicitações de Serviço
 * Aplica:
 *   RF07 — Controle de Concorrência (Optimistic Locking + Lock por serviço)
 *   RN03 — Política de Cancelamento (6 horas de antecedência)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitacaoService {

    private final SolicitacaoRepository solicitacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;

    @Transactional
    public Solicitacao criar(CriarSolicitacaoDTO dto, Long clienteId) {
        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado."));
        Usuario prestador = usuarioRepository.findById(dto.prestadorId())
                .orElseThrow(() -> new BusinessException("Prestador não encontrado."));

        // RN02: verifica visibilidade do prestador (deve ter serviço ativo)
        boolean temServicoAtivo = servicoRepository.existsByPrestadorAndAtivoTrue(prestador);
        if (!temServicoAtivo) {
            throw new BusinessException("RN02: Este prestador não está disponível no momento (sem serviços ativos).");
        }

        Servico servico = null;
        if (dto.servicoId() != null) {
            servico = servicoRepository.findById(dto.servicoId())
                    .orElse(null);
        }

        Solicitacao sol = Solicitacao.builder()
                .cliente(cliente)
                .prestador(prestador)
                .servico(servico)
                .descricao(dto.descricao())
                .dataHoraAgendada(dto.dataHoraAgendada())
                .endereco(dto.endereco())
                .valor(servico != null ? servico.getPrecoHora() : null)
                .status(Solicitacao.Status.PENDENTE)
                .build();

        return solicitacaoRepository.save(sol);
    }

    /**
     * RF07 — Aceitar Chamado com controle de concorrência.
     * Usa @Version (Optimistic Locking JPA) + validação de status único.
     * Impede dois prestadores aceitarem o mesmo serviço e bloqueia a agenda.
     */
    @Transactional
    public Solicitacao aceitar(Long solicitacaoId, Long prestadorId) {
        Solicitacao sol = solicitacaoRepository.findById(solicitacaoId)
                .orElseThrow(() -> new BusinessException("Solicitação não encontrada."));

        // RF07: verifica se já foi aceita por outro
        if (sol.getStatus() != Solicitacao.Status.PENDENTE) {
            throw new BusinessException("RF07: Esta solicitação já foi processada por outro prestador.");
        }

        // RF07: verifica conflito de agenda
        boolean agendaOcupada = solicitacaoRepository.existsConflitoDeAgenda(
                prestadorId, sol.getDataHoraAgendada());
        if (agendaOcupada) {
            throw new BusinessException("RF07: Conflito de agenda — você já possui um atendimento neste horário.");
        }

        sol.setStatus(Solicitacao.Status.ACEITO);
        sol.setAceitoEm(LocalDateTime.now());
        // O @Version garante que, se dois threads tentarem salvar simultaneamente,
        // um deles receberá OptimisticLockException
        return solicitacaoRepository.save(sol);
    }

    @Transactional
    public Solicitacao recusar(Long solicitacaoId) {
        Solicitacao sol = buscarValida(solicitacaoId);
        if (sol.getStatus() != Solicitacao.Status.PENDENTE) {
            throw new BusinessException("Só é possível recusar solicitações pendentes.");
        }
        sol.setStatus(Solicitacao.Status.RECUSADO);
        return solicitacaoRepository.save(sol);
    }

    /**
     * RN03 — Cancelamento sem penalidades: antecedência mínima de 6 horas.
     */
    @Transactional
    public Solicitacao cancelar(Long solicitacaoId) {
        Solicitacao sol = buscarValida(solicitacaoId);
        LocalDateTime agora = LocalDateTime.now();
        long horasRestantes = java.time.Duration.between(agora, sol.getDataHoraAgendada()).toHours();

        if (horasRestantes < 6) {
            throw new BusinessException(
                    "RN03: Cancelamento não permitido. Restam apenas " + horasRestantes +
                    "h para o atendimento. Mínimo exigido: 6 horas.");
        }

        sol.setStatus(Solicitacao.Status.CANCELADO);
        return solicitacaoRepository.save(sol);
    }

    @Transactional
    public Solicitacao concluir(Long solicitacaoId) {
        Solicitacao sol = buscarValida(solicitacaoId);
        if (sol.getStatus() != Solicitacao.Status.ACEITO) {
            throw new BusinessException("Só é possível concluir solicitações aceitas.");
        }
        sol.setStatus(Solicitacao.Status.AGUARDANDO_PAGAMENTO);
        return solicitacaoRepository.save(sol);
    }

    public List<Solicitacao> listarPorCliente(Long clienteId) {
        return solicitacaoRepository.findByClienteIdOrderByCriadoEmDesc(clienteId);
    }

    public List<Solicitacao> listarPorPrestador(Long prestadorId) {
        return solicitacaoRepository.findByPrestadorIdOrderByCriadoEmDesc(prestadorId);
    }

    public Solicitacao buscarPorId(Long id) {
        return buscarValida(id);
    }

    private Solicitacao buscarValida(Long id) {
        return solicitacaoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Solicitação #" + id + " não encontrada."));
    }
}
