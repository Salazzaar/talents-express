package com.talentsexpress.api.repository;

import com.talentsexpress.api.model.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByAvaliadoId(Long avaliadoId);

    long countByAvaliadoId(Long avaliadoId);

    @Query("SELECT AVG(a.nota) FROM Avaliacao a WHERE a.avaliado.id = :id")
    Optional<Double> findMediaByAvaliado(@Param("id") Long id);

    /** Verifica se já existe avaliação para determinada solicitação (evita duplicata) */
    Optional<Avaliacao> findBySolicitacaoId(Long solicitacaoId);
}
