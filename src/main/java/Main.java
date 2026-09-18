import config.ConexaoBanco;
import dao.RegistroSaidaDAO;
import model.Usuario;
import service.AlunoService;
import service.AutenticacaoService;
import service.RegistroSaidaService;
import service.RegraNegocioException;
import service.ResponsavelService;
import service.TurmaService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Ponto de entrada da aplicação.
 * Por enquanto demonstra o fluxo completo do sistema através da camada
 * service, que concentra as regras de negócio; os DAOs cuidam só do acesso
 * puro a dados.
 */
public class Main {

    public static void main(String[] args) {
        try (Connection conexao = ConexaoBanco.conectar()) {

            TurmaService turmaService = new TurmaService(conexao);
            AlunoService alunoService = new AlunoService(conexao);
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            RegistroSaidaService registroSaidaService = new RegistroSaidaService(conexao);
            AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);

            // 1. Cadastro básico
            int turmaId = turmaService.inserir("2º Ano A");
            int alunoYasminId = alunoService.inserir("Yasmin Ferreira", turmaId);
            int alunoPedroId = alunoService.inserir("Pedro Souza", turmaId);

            // 2. Cadastro de responsável e vínculo com a aluna que ele pode retirar
            int responsavelId = responsavelService.inserir("Maria Ferreira", null);
            responsavelService.vincularAluno(alunoYasminId, responsavelId);

            // 3. Totem: responsável chega e seleciona a aluna Yasmin
            int registroId = registroSaidaService.chamarAluno(alunoYasminId, responsavelId);
            System.out.println("Yasmin foi chamada! (registro id=" + registroId + ")");

            imprimirPainel(registroSaidaService, turmaId);

            // 4. Regra nova: tentar chamar a mesma aluna de novo no mesmo dia
            try {
                registroSaidaService.chamarAluno(alunoYasminId, responsavelId);
            } catch (RegraNegocioException e) {
                System.out.println("\nBloqueio esperado (chamada duplicada): " + e.getMessage());
            }

            // 5. Retirada NÃO autorizada (Pedro não tem esse responsável vinculado)
            try {
                registroSaidaService.chamarAluno(alunoPedroId, responsavelId);
            } catch (RegraNegocioException e) {
                System.out.println("\nBloqueio esperado (não autorizado): " + e.getMessage());
            }

            // 6. Autenticação: cadastro de uma professora e login
            autenticacaoService.cadastrar("Carla Souza", "carla@escola.com", "senha123", Usuario.PERFIL_PROFESSOR, null);
            System.out.println("\nProfessora cadastrada.");

            Usuario logada = autenticacaoService.autenticar("carla@escola.com", "senha123");
            System.out.println("Login OK: " + logada);

            // 7. Regra nova: senha errada deve ser bloqueada com mensagem genérica
            try {
                autenticacaoService.autenticar("carla@escola.com", "senhaErrada");
            } catch (RegraNegocioException e) {
                System.out.println("\nBloqueio esperado (senha errada): " + e.getMessage());
            }

            // 8. Regra nova: e-mail duplicado no cadastro
            try {
                autenticacaoService.cadastrar("Outra Carla", "carla@escola.com", "outraSenha", Usuario.PERFIL_PROFESSOR, null);
            } catch (RegraNegocioException e) {
                System.out.println("\nBloqueio esperado (e-mail duplicado): " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erro de banco de dados: " + e.getMessage());
        } catch (RegraNegocioException e) {
            System.out.println("Erro de regra de negócio: " + e.getMessage());
        }
    }

    private static void imprimirPainel(RegistroSaidaService registroSaidaService, int turmaId) throws SQLException {
        System.out.println("\n--- Painel da sala (turma " + turmaId + ") ---");
        List<RegistroSaidaDAO.StatusAluno> status = registroSaidaService.statusPorTurma(turmaId);
        for (RegistroSaidaDAO.StatusAluno linha : status) {
            System.out.println(linha);
        }
    }
}