package io.github.obrenoxs.trilhatech.assistente.chat.controller;

import io.github.obrenoxs.trilhatech.assistente.chat.dto.request.ChatRequest;
import io.github.obrenoxs.trilhatech.assistente.chat.dto.response.ChatResponse;
import io.github.obrenoxs.trilhatech.assistente.chat.service.AssistenteService;
import io.github.obrenoxs.trilhatech.assistente.uso.limitador.IpDoCliente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/assistente")
public class AssistenteController {

    private final AssistenteService assistenteService;

    public AssistenteController(AssistenteService assistenteService) {
        this.assistenteService = assistenteService;
    }

    @PostMapping(value = "/mensagens")
    public ResponseEntity<ChatResponse> responder(
            @Valid @RequestBody ChatRequest requisicao,
            HttpServletRequest request
    ) {
        String visitante = IpDoCliente.escolher(request.getHeader("X-Real-IP"), request.getRemoteAddr());
        ChatResponse resposta = assistenteService.responder(requisicao, visitante);
        return ResponseEntity.ok().body(resposta);
    }
}
