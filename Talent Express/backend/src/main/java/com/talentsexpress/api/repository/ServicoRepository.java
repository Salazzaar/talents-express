package com.talentsexpress.api.repository;

import com.talentsexpress.api.model.Servico;
import com.talentsexpress.api.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByPrestador(Usuario prestador);

    boolean existsByPrestadorAndAtivoTrue(Usuario prestador);

    /**
     * RN02: Só retorna serviços ativos de prestadores com perfil completo.
     * Aplica filtros de texto (título/descrição), categoria e bairro.
     *
     * NOTA: A categoria é comparada pelo nome do enum (BRACAL, INTELECTUAL, ARTISTICO).
     * O frontend deve enviar o valor do enum, não a descrição com acentos.
     */
    @Query(value = """
        SELECT s.* FROM servicos s
        JOIN usuarios p ON s.prestador_id = p.id
        WHERE s.ativo = true
          AND p.ativo = true
          AND p.perfil_completo = true
          AND (:texto IS NULL OR LOWER(s.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(s.descricao) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :texto, '%')))
          AND (:categoria IS NULL OR s.categoria = :categoria)
          AND (:bairro IS NULL OR LOWER(p.bairro) = LOWER(:bairro))
        ORDER BY s.criado_em DESC
        """, nativeQuery = true)
    List<Servico> findElegiveis(
            @Param("texto") String texto,
            @Param("categoria") String categoria,
            @Param("bairro") String bairro);
}
