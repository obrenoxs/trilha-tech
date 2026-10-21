package io.github.obrenoxs.trilhatech.quiz.pergunta.repository;

import io.github.obrenoxs.trilhatech.quiz.pergunta.entity.Alternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AlternativaRepository extends JpaRepository<Alternativa, UUID> {

    @Query("select a from Alternativa a order by a.pergunta.ordem, a.ordem")
    List<Alternativa> findAllOrdenadas();

    @Query("select a from Alternativa a join fetch a.pergunta join fetch a.areaPrincipal join fetch a.areaSecundaria order by a.pergunta.ordem, a.ordem")
    List<Alternativa> findAllComAreas();
}
