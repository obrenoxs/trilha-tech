package io.github.obrenoxs.trilhatech.conteudo.trilha.repository;

import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecursoRepository extends JpaRepository<Recurso, UUID> {
}
