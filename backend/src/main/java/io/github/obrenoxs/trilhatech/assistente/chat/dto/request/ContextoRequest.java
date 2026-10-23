package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ContextoRequest(

        @Schema(description = "Slug da área aberta na página", example = "desenvolvimento-de-software")
        @Pattern(regexp = "^[a-z0-9-]{1,80}$")
        String areaSlug,

        @Schema(description = "Slug da trilha aberta na página. Se vier, vale mais que a área", example = "java-e-spring")
        @Pattern(regexp = "^[a-z0-9-]{1,80}$")
        String trilhaSlug,

        @Schema(description = "Slugs das áreas do resultado do quiz, da mais indicada para a menos (no máximo 3)")
        @Size(max = 3)
        List<@NotNull @Pattern(regexp = "^[a-z0-9-]{1,80}$") String> resultadoQuiz
) {}
