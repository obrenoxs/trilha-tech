package io.github.obrenoxs.trilhatech.quiz.pergunta.repository;

import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Pergunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PerguntaRepository extends JpaRepository<Pergunta, UUID> {

    List<Pergunta> findAllByOrderByOrdemAsc();
}
