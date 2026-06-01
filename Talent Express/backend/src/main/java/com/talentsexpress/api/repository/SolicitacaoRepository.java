package com.talentsexpress.api.repository;

import com.talentsexpress.api.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Long> {

    List<Solicitacao> findByClienteIdOrderByCriadoEmDesc(Long clienteId);
    List<Solicitacao> findByPrestadorIdOrderByCriadoEmDesc(Long prestadorId);

    /**
     * RF07: Verifica conflito de agenda para um prestador num dado horário.
     * Considera janela de ±2 horas ao redor do horário agendado.
     */
    @Query("""
        SELECT COUNT(s) > 0 FROM Solicitacao s
        WHERE s.prestador.id = :prestadorId
          AND s.status = 'ACEITO'
          AND s.dataHoraAgendada BETWEEN :inicio AND :fim
        """)
    boolean existsConflitoDeAgenda(
            @Param("prestadorId") Long prestadorId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    default boolean existsConflitoDeAgenda(Long prestadorId, LocalDateTime dataHora) {
        return existsConflitoDeAgenda(
                prestadorId,
                dataHora.minusHours(2),
                dataHora.plusHours(2));
    }
}
