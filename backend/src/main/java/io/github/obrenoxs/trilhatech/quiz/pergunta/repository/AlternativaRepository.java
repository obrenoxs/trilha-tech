package io.github.obrenoxs.trilhatech.quiz.pergunta.repository;

import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AlternativaRepository extends JpaRepository<Alternativa, UUID> {
}
