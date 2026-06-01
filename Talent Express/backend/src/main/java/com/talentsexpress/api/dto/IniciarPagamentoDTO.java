package com.talentsexpress.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record IniciarPagamentoDTO(
        @NotNull Long solicitacaoId,
        @NotBlank String metodo,   // "pix", "credito", "debito"
        @NotNull BigDecimal valor
) {}
