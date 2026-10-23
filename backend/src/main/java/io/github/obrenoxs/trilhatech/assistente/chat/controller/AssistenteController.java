package io.github.obrenoxs.trilhatech.assistente.chat.controller;

import io.github.obrenoxs.trilhatech.assistente.chat.dto.request.ChatRequest;
import io.github.obrenoxs.trilhatech.assistente.chat.dto.response.ChatResponse;
import io.github.obrenoxs.trilhatech.assistente.chat.service.AssistenteService;
import io.github.obrenoxs.trilhatech.assistente.uso.limitador.IpDoCliente;
import io.github.obrenoxs.trilhatech.compartilhado.dto.response.ErroResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Assistente", description = "O assistente de IA sobre carreira em TI")
@RestController
@RequestMapping(value = "/api/assistente")
public class AssistenteController {

    private final AssistenteService assistenteService;

    public AssistenteController(AssistenteService assistenteService) {
        this.assistenteService = assistenteService;
    }

    @Operation(summary = "Envia uma mensagem ao assistente",
            description = "Responde sobre carreira em TI, as áreas e as trilhas do Trilha Tech. "
                    + "O servidor não guarda a conversa: o front-end reenvia as últimas mensagens em 'historico' "
                    + "(no máximo 6), e o contexto da página (área, trilha e resultado do quiz) como slugs, "
                    + "que o servidor confere no banco. O histórico é só conversa e nunca altera as regras do assistente. "
                    + "Há limites de uso por visitante e por dia: veja os códigos 429 e 503.")
    @ApiResponse(responseCode = "200", description = "A resposta do assistente")
    @ApiResponse(responseCode = "400", description = "Mensagem, histórico ou contexto inválidos",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "413", description = "Corpo da requisição grande demais",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "429", description = "O visitante enviou mensagens demais em pouco tempo",
            headers = @Header(name = "Retry-After", description = "Segundos até poder tentar de novo",
                    schema = @Schema(type = "integer", example = "60")),
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @ApiResponse(responseCode = "503", description = "O assistente está indisponível (limite global ou diário atingido, ou o provedor de IA fora do ar). O restante do site continua funcionando",
            content = @Content(schema = @Schema(implementation = ErroResponse.class)))
    @PostMapping(value = "/mensagens")
    public ResponseEntity<ChatResponse> responder(
            @Valid @RequestBody ChatRequest requisicao,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        String visitante = IpDoCliente.escolher(request.getHeader("X-Real-IP"), request.getRemoteAddr());
        ChatResponse resposta = assistenteService.responder(requisicao, visitante);
        return ResponseEntity.ok().body(resposta);
    }
}