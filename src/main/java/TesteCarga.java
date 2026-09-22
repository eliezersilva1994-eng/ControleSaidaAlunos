import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.ConexaoBanco;
import model.Usuario;
import service.AlunoService;
import service.AutenticacaoService;
import service.ResponsavelService;
import service.RegraNegocioException;
import service.TurmaService;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Teste de carga: simula o horário de pico da saída — várias famílias
 * usando o totem ao mesmo tempo, ao longo de um período de tempo.
 *
 * O que este programa faz, em ordem:
 * 1. Limpa dados de teste anteriores (tudo prefixado com "CARGA-" —
 *    nunca mexe nos seus dados reais)
 * 2. Cria turmas, alunos e responsáveis de teste (por padrão, ~280 alunos)
 * 3. Cria (ou reaproveita) um login de totem para autenticar
 * 4. Dispara as chamadas de "retirar aluno" via HTTP de verdade,
 *    simulando alguns tablets em uso simultâneo, espalhadas ao
 *    longo do tempo configurado abaixo
 * 5. No final, imprime um relatório: quantas deram certo, tempo
 *    de resposta médio/mínimo/máximo, e erros encontrados
 *
 * IMPORTANTE: rode com o servidor (SistemaSaidaAlunosApplication) já
 * no ar antes de executar este programa.
 */
public class TesteCarga {

    // ---- Ajuste esses números para calibrar o teste ----
    private static final int TOTAL_TURMAS = 10;
    private static final int ALUNOS_POR_TURMA = 28; // 10 x 28 = 280 alunos
    private static final int TABLETS_SIMULTANEOS = 4; // quantos tablets "em uso" ao mesmo tempo
    private static final int DURACAO_TOTAL_SEGUNDOS = 600; // 10 minutos — diminua para testar mais rápido
    private static final String URL_BASE = "http://localhost:8080";
    private static final String EMAIL_TOTEM_TESTE = "carga-teste-totem@escola.com";
    private static final String SENHA_TOTEM_TESTE = "carga123456";

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper json = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        try (Connection conexao = ConexaoBanco.conectar()) {

            System.out.println("1. Limpando dados de teste anteriores...");
            limparDadosDeTeste(conexao);

            System.out.println("2. Criando turmas, alunos e responsáveis de teste...");
            List<int[]> pares = semearDados(conexao); // cada item: {alunoId, responsavelId}
            System.out.println("   " + pares.size() + " alunos prontos para o teste.");

            System.out.println("3. Preparando login de totem para o teste...");
            String token = prepararLoginEObterToken(conexao);

            System.out.println("4. Disparando " + pares.size() + " chamadas, simulando "
                    + TABLETS_SIMULTANEOS + " tablets ao longo de "
                    + (DURACAO_TOTAL_SEGUNDOS / 60.0) + " minutos...\n");
            executarTesteDeCarga(pares, token);
        }
    }

    private static void limparDadosDeTeste(Connection conexao) throws SQLException {
        try (Statement stmt = conexao.createStatement()) {
            stmt.executeUpdate(
                    "DELETE FROM registro_saida WHERE aluno_id IN (SELECT id FROM aluno WHERE nome LIKE 'CARGA-%')");
            stmt.executeUpdate(
                    "DELETE FROM aluno_responsavel WHERE aluno_id IN (SELECT id FROM aluno WHERE nome LIKE 'CARGA-%')");
            stmt.executeUpdate("DELETE FROM aluno WHERE nome LIKE 'CARGA-%'");
            stmt.executeUpdate("DELETE FROM turma WHERE nome LIKE 'CARGA-%'");
            stmt.executeUpdate("DELETE FROM responsavel WHERE nome LIKE 'CARGA-%'");
        }
    }

    private static List<int[]> semearDados(Connection conexao) throws SQLException, RegraNegocioException {
        TurmaService turmaService = new TurmaService(conexao);
        AlunoService alunoService = new AlunoService(conexao);
        ResponsavelService responsavelService = new ResponsavelService(conexao);

        List<int[]> pares = new ArrayList<>();

        for (int t = 1; t <= TOTAL_TURMAS; t++) {
            int turmaId = turmaService.inserir("CARGA-Turma-" + t);

            for (int a = 1; a <= ALUNOS_POR_TURMA; a++) {
                int alunoId = alunoService.inserir("CARGA-Aluno-" + t + "-" + a, turmaId);
                int responsavelId = responsavelService.inserir("CARGA-Responsavel-" + t + "-" + a, null);
                responsavelService.vincularAluno(alunoId, responsavelId);
                pares.add(new int[]{alunoId, responsavelId});
            }
        }
        return pares;
    }

    private static String prepararLoginEObterToken(Connection conexao) throws Exception {
        AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);
        try {
            autenticacaoService.cadastrar("Totem Teste de Carga", EMAIL_TOTEM_TESTE, SENHA_TOTEM_TESTE,
                    Usuario.PERFIL_TOTEM, null);
        } catch (RegraNegocioException jaExiste) {
            // Login de teste já existe de uma rodada anterior — tudo bem, reaproveita.
        }

        String corpo = String.format("{\"email\":\"%s\",\"senha\":\"%s\"}", EMAIL_TOTEM_TESTE, SENHA_TOTEM_TESTE);
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(corpo))
                .build();

        HttpResponse<String> resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
        if (resposta.statusCode() != 200) {
            throw new RuntimeException("Não foi possível logar como totem de teste: " + resposta.body());
        }
        JsonNode dados = json.readTree(resposta.body());
        return dados.get("token").asText();
    }

    private static void executarTesteDeCarga(List<int[]> pares, String token) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(TABLETS_SIMULTANEOS);
        CopyOnWriteArrayList<Long> temposDeRespostaMs = new CopyOnWriteArrayList<>();
        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);
        List<String> mensagensDeErro = new CopyOnWriteArrayList<>();

        // Divide as chamadas entre os "tablets" (uma lista por thread).
        List<List<int[]>> lotes = new ArrayList<>();
        for (int i = 0; i < TABLETS_SIMULTANEOS; i++) lotes.add(new ArrayList<>());
        for (int i = 0; i < pares.size(); i++) {
            lotes.get(i % TABLETS_SIMULTANEOS).add(pares.get(i));
        }

        // Espaço médio entre chamadas de um mesmo tablet, para espalhar
        // o total ao longo da duração configurada.
        int chamadasPorTablet = Math.max(1, pares.size() / TABLETS_SIMULTANEOS);
        long esperaMediaMs = (DURACAO_TOTAL_SEGUNDOS * 1000L) / chamadasPorTablet;

        long inicio = System.currentTimeMillis();
        CountDownLatch fim = new CountDownLatch(TABLETS_SIMULTANEOS);

        for (List<int[]> lote : lotes) {
            executor.submit(() -> {
                try {
                    for (int[] par : lote) {
                        long antes = System.currentTimeMillis();
                        try {
                            String corpo = String.format("{\"alunoId\":%d,\"responsavelId\":%d}", par[0], par[1]);
                            HttpRequest requisicao = HttpRequest.newBuilder()
                                    .uri(URI.create(URL_BASE + "/api/registros/chamar"))
                                    .header("Content-Type", "application/json")
                                    .header("Authorization", "Bearer " + token)
                                    .POST(HttpRequest.BodyPublishers.ofString(corpo))
                                    .build();

                            HttpResponse<String> resposta =
                                    httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
                            long depois = System.currentTimeMillis();
                            temposDeRespostaMs.add(depois - antes);

                            if (resposta.statusCode() == 200) {
                                sucessos.incrementAndGet();
                            } else {
                                falhas.incrementAndGet();
                                mensagensDeErro.add("HTTP " + resposta.statusCode() + ": " + resposta.body());
                            }
                        } catch (Exception e) {
                            falhas.incrementAndGet();
                            mensagensDeErro.add(e.getClass().getSimpleName() + ": " + e.getMessage());
                        }

                        // Pausa (com um pouco de variação) para não disparar tudo de uma vez.
                        long pausa = (long) (esperaMediaMs * (0.5 + Math.random()));
                        Thread.sleep(pausa);
                    }
                } catch (InterruptedException ignorada) {
                } finally {
                    fim.countDown();
                }
            });
        }

        fim.await();
        executor.shutdown();
        long duracaoTotalMs = System.currentTimeMillis() - inicio;

        imprimirRelatorio(pares.size(), sucessos.get(), falhas.get(), temposDeRespostaMs, duracaoTotalMs, mensagensDeErro);
    }

    private static void imprimirRelatorio(int total, int sucessos, int falhas,
                                          List<Long> tempos, long duracaoTotalMs, List<String> erros) {
        System.out.println("\n========== RELATÓRIO DO TESTE DE CARGA ==========");
        System.out.println("Total de chamadas: " + total);
        System.out.printf("Sucesso: %d (%.1f%%)%n", sucessos, 100.0 * sucessos / total);
        System.out.println("Falhas: " + falhas);
        System.out.printf("Duração total: %.1f segundos (%.1f minutos)%n",
                duracaoTotalMs / 1000.0, duracaoTotalMs / 60000.0);

        if (!tempos.isEmpty()) {
            List<Long> ordenados = new ArrayList<>(tempos);
            Collections.sort(ordenados);
            long min = ordenados.get(0);
            long max = ordenados.get(ordenados.size() - 1);
            double media = ordenados.stream().mapToLong(Long::longValue).average().orElse(0);
            int indiceP95 = Math.min(ordenados.size() - 1, (int) (ordenados.size() * 0.95));
            long p95 = ordenados.get(indiceP95);

            System.out.println("\nTempo de resposta de cada chamada:");
            System.out.println("  Mínimo: " + min + " ms");
            System.out.printf("  Médio: %.1f ms%n", media);
            System.out.println("  Máximo: " + max + " ms");
            System.out.println("  95% das respostas em até: " + p95 + " ms");
        }

        if (!erros.isEmpty()) {
            System.out.println("\nExemplos de erro (até 5 diferentes):");
            erros.stream().distinct().limit(5).forEach(e -> System.out.println("  - " + e));
        }

        System.out.println("\nOs dados de teste (prefixo \"CARGA-\") podem ser removidos rodando");
        System.out.println("este programa de novo (ele limpa antes de começar) ou manualmente pelo pgAdmin.");
        System.out.println("===================================================");
    }
}