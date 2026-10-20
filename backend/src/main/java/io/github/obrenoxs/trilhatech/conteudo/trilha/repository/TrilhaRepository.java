package io.github.obrenoxs.trilhatech.conteudo.trilha.repository;

import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Trilha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrilhaRepository extends JpaRepository<Trilha, UUID> {

    Optional<Trilha> findBySlug(String slug);

    long countByAreaId(UUID areaId);

    List<Trilha> findAllByAreaIdOrderByOrdemNaAreaAsc(UUID areaId);
}
