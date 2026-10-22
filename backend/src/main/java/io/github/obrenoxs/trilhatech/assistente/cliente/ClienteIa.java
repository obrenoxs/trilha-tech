package io.github.obrenoxs.trilhatech.assistente.cliente;

import java.util.List;

public interface ClienteIa {

    String responder(String instrucaoSistema, List<MensagemIa> conversa);
}
