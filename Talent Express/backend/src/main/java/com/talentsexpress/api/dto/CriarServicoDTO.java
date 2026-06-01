package com.talentsexpress.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record CriarServicoDTO(
        String titulo,
        String descricao,
        String categoria,      // "Braçal", "Intelectual", "Artístico"
        BigDecimal preco,
        List<String> diasDisponiveis,
        String horaInicio,
        String horaFim,
        Boolean ativo
) {}
