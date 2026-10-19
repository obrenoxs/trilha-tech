package io.github.obrenoxs.trilhatech.content.trilha.repository;

import io.github.obrenoxs.trilhatech.content.trilha.entity.Trilha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrilhaRepository extends JpaRepository<Trilha, UUID> {

    Optional<Trilha> findBySlug(String slug);
}
