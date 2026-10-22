package io.github.obrenoxs.trilhatech.conteudo.trilha.service;

import io.github.obrenoxs.trilhatech.compartilhado.exception.NaoEncontradoException;
import io.github.obrenoxs.trilhatech.conteudo.trilha.entity.Trilha;
import io.github.obrenoxs.trilhatech.conteudo.trilha.repository.TrilhaRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoadmapPdfService {

    private static final String PASTA_DOS_PDFS = "conteudo/pdfs/";

    private final TrilhaRepository trilhaRepository;

    public RoadmapPdfService(TrilhaRepository trilhaRepository) {
        this.trilhaRepository = trilhaRepository;
    }

    public Resource obter(String slug) {
        Trilha trilha = trilhaRepository.findBySlug(slug)
                .orElseThrow(() -> new NaoEncontradoException("Trilha não encontrada: " + slug));

        Resource pdf = new ClassPathResource(PASTA_DOS_PDFS + "roadmap-" + trilha.getSlug() + ".pdf");
        if (!pdf.exists()) {
            throw new NaoEncontradoException("PDF não disponível para a trilha: " + slug);
        }

        return pdf;
    }
}
