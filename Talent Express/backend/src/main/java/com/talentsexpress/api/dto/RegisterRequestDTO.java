package com.talentsexpress.api.dto;

import com.talentsexpress.api.model.Usuario;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record RegisterRequestDTO(
        @NotBlank String nome,
        @Email @NotBlank String email,
        @NotBlank String cpfCnpj,
        @NotBlank @Size(min = 8) String senha,
        @NotNull Usuario.Perfil perfil,
        @NotNull LocalDate dataNascimento,
        String telefone,
        String bairro   // Bairro de atuação (obrigatório para perfilCompleto = true no matching)
) {}

