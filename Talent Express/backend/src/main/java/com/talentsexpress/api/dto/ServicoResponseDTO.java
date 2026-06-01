package com.talentsexpress.api.dto;

import com.talentsexpress.api.model.Servico;
import java.math.BigDecimal;
import java.util.List;

public record ServicoResponseDTO(
        Long id,
        Long prestadorId,
        String prestadorNome,
        String titulo,
        String descricao,
        String categoria,
        BigDecimal preco,
        boolean ativo,
        List<String> diasDisponiveis,
        String horaInicio,
        String horaFim
) {
    public static ServicoResponseDTO from(Servico s) {
        return new ServicoResponseDTO(
                s.getId(),
                s.getPrestador().getId(),
                s.getPrestador().getNome(),
                s.getTitulo(),
                s.getDescricao(),
                s.getCategoria().getDescricao(),  // retorna "Braçal", "Intelectual", "Artístico"
                s.getPrecoHora(),
                s.isAtivo(),
                s.getDiasDisponiveis(),
                s.getHoraInicio(),
                s.getHoraFim()
        );
    }
}
