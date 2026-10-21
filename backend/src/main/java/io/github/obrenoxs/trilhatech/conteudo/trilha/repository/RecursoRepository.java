package io.github.obrenoxs.trilhatech.conteudo.trilha.repository;

import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecursoRepository extends JpaRepository<Recurso, UUID> {

    @Query("select r from Recurso r where r.etapa.trilha.id = :trilhaId order by r.etapa.ordem, r.ordem")
    List<Recurso> findAllByTrilhaId(@Param("trilhaId") UUID trilhaId);
}
