package com.talentsexpress.api.service;

import com.talentsexpress.api.dto.CriarServicoDTO;
import com.talentsexpress.api.dto.ServicoResponseDTO;
import com.talentsexpress.api.model.Servico;
import com.talentsexpress.api.model.Usuario;
import com.talentsexpress.api.repository.ServicoRepository;
import com.talentsexpress.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Camada de Negócio — Serviços do Portfólio do Prestador
 * Aplica RN02: prestador só aparece nas buscas com ao menos 1 serviço ativo
 */
@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final UsuarioRepository usuarioRepository;

    /** Lista todos os serviços ativos (usado pelo Matching e catálogo público). */
    public List<ServicoResponseDTO> listarAtivos() {
        return servicoRepository.findAll().stream()
                .filter(Servico::isAtivo)
                .map(ServicoResponseDTO::from)
                .toList();
    }

    /** Lista os serviços de um prestador específico (painel do prestador). */
    public List<ServicoResponseDTO> listarPorPrestador(Long prestadorId) {
        Usuario prestador = buscarUsuario(prestadorId);
        return servicoRepository.findByPrestador(prestador).stream()
                .map(ServicoResponseDTO::from)
                .toList();
    }

    /** Cria um novo serviço para o prestador autenticado. */
    @Transactional
    public ServicoResponseDTO criar(CriarServicoDTO dto, Long prestadorId) {
        Usuario prestador = buscarUsuario(prestadorId);

        if (prestador.getPerfil() != Usuario.Perfil.PRESTADOR) {
            throw new BusinessException("Apenas prestadores podem cadastrar serviços.");
        }

        Servico servico = Servico.builder()
                .prestador(prestador)
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .categoria(parseCategoria(dto.categoria()))
                .precoHora(dto.preco())
                .diasDisponiveis(dto.diasDisponiveis())
                .horaInicio(dto.horaInicio())
                .horaFim(dto.horaFim())
                .ativo(dto.ativo() != null ? dto.ativo() : true)
                .build();

        return ServicoResponseDTO.from(servicoRepository.save(servico));
    }

    /** Atualiza um serviço existente (somente campos não nulos). */
    @Transactional
    public ServicoResponseDTO atualizar(Long id, CriarServicoDTO dto) {
        Servico servico = buscarServico(id);

        if (dto.titulo()         != null) servico.setTitulo(dto.titulo());
        if (dto.descricao()      != null) servico.setDescricao(dto.descricao());
        if (dto.categoria()      != null) servico.setCategoria(parseCategoria(dto.categoria()));
        if (dto.preco()          != null) servico.setPrecoHora(dto.preco());
        if (dto.diasDisponiveis()!= null) servico.setDiasDisponiveis(dto.diasDisponiveis());
        if (dto.horaInicio()     != null) servico.setHoraInicio(dto.horaInicio());
        if (dto.horaFim()        != null) servico.setHoraFim(dto.horaFim());
        if (dto.ativo()          != null) servico.setAtivo(dto.ativo());

        return ServicoResponseDTO.from(servicoRepository.save(servico));
    }

    /** Exclui (hard delete) um serviço. */
    @Transactional
    public void excluir(Long id) {
        if (!servicoRepository.existsById(id)) {
            throw new BusinessException("Serviço #" + id + " não encontrado.");
        }
        servicoRepository.deleteById(id);
    }

    /** Busca serviço por ID ou lança erro. */
    public ServicoResponseDTO buscarPorId(Long id) {
        return ServicoResponseDTO.from(buscarServico(id));
    }

    // ── helpers privados ──────────────────────────────────────────────────────

    private Servico buscarServico(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Serviço #" + id + " não encontrado."));
    }

    private Usuario buscarUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário #" + id + " não encontrado."));
    }

    private Servico.Categoria parseCategoria(String cat) {
        if (cat == null) throw new BusinessException("Categoria não informada.");
        // Aceita tanto os nomes do enum (BRACAL, INTELECTUAL, ARTISTICO)
        // quanto aliases em português enviados pelo frontend (braçal, intelectual, artístico)
        String normalizado = cat.trim().toUpperCase()
                .replace("Ç", "C").replace("Á", "A").replace("Í", "I")
                .replace("Ã", "A").replace("Ê", "E");
        try {
            return Servico.Categoria.valueOf(normalizado);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Categoria inválida: '" + cat +
                    "'. Use: BRACAL, INTELECTUAL ou ARTISTICO.");
        }
    }
}
