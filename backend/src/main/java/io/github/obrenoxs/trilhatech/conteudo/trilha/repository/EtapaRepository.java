package io.github.obrenoxs.trilhatech.conteudo.trilha.repository;

import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EtapaRepository extends JpaRepository<Etapa, UUID> {

    @Query("select coalesce(sum(e.duracaoSemanas), 0L) from Etapa e where e.trilha.id = :trilhaId")
    Long somarSemanasPorTrilha(@Param("trilhaId") UUID trilhaId);
}
