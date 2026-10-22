package io.github.obrenoxs.trilhatech.assistente.uso.limitador;

public class IpDoCliente {

    private static final int TAMANHO_MAXIMO_DE_UM_IP = 45;

    private IpDoCliente() {
    }

    public static String escolher(String cabecalhoXRealIp, String enderecoDaConexao) {
        if (pareceUmIp(cabecalhoXRealIp)) {
            return cabecalhoXRealIp.trim();
        }
        if (enderecoDaConexao == null || enderecoDaConexao.isBlank()) {
            return "desconhecido";
        }
        return enderecoDaConexao;
    }

    static boolean pareceUmIp(String valor) {
        if (valor == null) {
            return false;
        }

        String texto = valor.trim();
        if (texto.isEmpty() || texto.length() > TAMANHO_MAXIMO_DE_UM_IP) {
            return false;
        }

        for (int i = 0; i < texto.length(); i++) {
            char c = texto.charAt(i);
            boolean valido = (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F')
                    || c == ':'
                    || c == '.';
            if (!valido) {
                return false;
            }
        }

        return texto.contains(".") || texto.contains(":");
    }
}
