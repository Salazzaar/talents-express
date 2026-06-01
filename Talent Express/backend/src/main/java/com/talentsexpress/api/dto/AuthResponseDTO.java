package com.talentsexpress.api.dto;

import com.talentsexpress.api.model.Usuario;

public record AuthResponseDTO(
        Long id,
        String nome,
        String email,
        Usuario.Perfil perfil,
        String token
) {}
