package config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Bloqueia tentativas repetidas de login com a mesma conta, dificultando
 * ataques de força bruta (tentar várias senhas em sequência até acertar).
 *
 * Guarda as tentativas em memória (não no banco) — simples e suficiente
 * para o porte deste sistema. Reinicia sozinho se o servidor reiniciar,
 * o que é aceitável aqui.
 */
public class LimitadorTentativas {

    private static final int LIMITE_TENTATIVAS = 5;
    private static final long JANELA_MS = 15 * 60 * 1000L; // 15 minutos

    private static final ConcurrentHashMap<String, Registro> tentativas = new ConcurrentHashMap<>();

    public static boolean estaBloqueado(String email) {
        Registro registro = tentativas.get(normalizar(email));
        if (registro == null) {
            return false;
        }
        if (System.currentTimeMillis() - registro.inicioJanela > JANELA_MS) {
            tentativas.remove(normalizar(email));
            return false;
        }
        return registro.contagem >= LIMITE_TENTATIVAS;
    }

    public static void registrarFalha(String email) {
        tentativas.compute(normalizar(email), (chave, registroAtual) -> {
            long agora = System.currentTimeMillis();
            if (registroAtual == null || agora - registroAtual.inicioJanela > JANELA_MS) {
                return new Registro(1, agora);
            }
            return new Registro(registroAtual.contagem + 1, registroAtual.inicioJanela);
        });
    }

    public static void limpar(String email) {
        tentativas.remove(normalizar(email));
    }

    private static String normalizar(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static class Registro {
        final int contagem;
        final long inicioJanela;

        Registro(int contagem, long inicioJanela) {
            this.contagem = contagem;
            this.inicioJanela = inicioJanela;
        }
    }
}