package io.github.obrenoxs.trilhatech.content.trilha.repository;

import io.github.obrenoxs.trilhatech.content.trilha.entity.Etapa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EtapaRepository extends JpaRepository<Etapa, UUID> {
}
