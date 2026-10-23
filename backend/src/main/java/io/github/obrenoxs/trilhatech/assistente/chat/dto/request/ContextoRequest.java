package io.github.obrenoxs.trilhatech.assistente.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ContextoRequest(

        @Pattern(regexp = "^[a-z0-9-]{1,80}$")
        String areaSlug,

        @Pattern(regexp = "^[a-z0-9-]{1,80}$")
        String trilhaSlug,

        @Size(max = 3)
        List<@NotNull @Pattern(regexp = "^[a-z0-9-]{1,80}$") String> resultadoQuiz
) {
}
