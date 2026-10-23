package io.github.obrenoxs.trilhatech.tests;

public final class GuardaDoBancoDeTeste {

    private static final String SUFIXO = "_test";

    private GuardaDoBancoDeTeste() {
    }

    public static boolean nomeEhDeTeste(String nomeDoBanco) {
        return nomeDoBanco != null && nomeDoBanco.endsWith(SUFIXO);
    }

    public static String nomeDoBancoNaUrl(String urlJdbc) {
        if (urlJdbc == null) {
            return null;
        }
        int parametros = urlJdbc.indexOf('?');
        String semParametros = parametros >= 0 ? urlJdbc.substring(0, parametros) : urlJdbc;
        return semParametros.substring(semParametros.lastIndexOf('/') + 1);
    }

    public static void exigirUrl(String urlJdbc) {
        exigirNome(nomeDoBancoNaUrl(urlJdbc));
    }

    public static void exigirNome(String nomeDoBanco) {
        if (!nomeEhDeTeste(nomeDoBanco)) {
            throw new IllegalStateException("Os testes de integração só rodam em um banco cujo nome termina em '"
                    + SUFIXO + "', mas o banco configurado é '" + nomeDoBanco + "'");
        }
    }
}