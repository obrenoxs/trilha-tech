package io.github.obrenoxs.trilhatech.conteudo.trilha.repository;

import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.FaixaSalarial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FaixaSalarialRepository extends JpaRepository<FaixaSalarial, UUID> {

    List<FaixaSalarial> findAllByTrilhaId(UUID trilhaId);
}
