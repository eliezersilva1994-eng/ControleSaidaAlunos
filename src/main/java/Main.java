import dao.AlunoDAO;
import dao.RegistroSaidaDAO;
import dao.ResponsavelDAO;
import dao.TurmaDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Ponto de entrada da aplicação.
 * Por enquanto demonstra o fluxo completo do sistema; a lógica real
 * de acesso a dados vive nas classes do pacote dao.
 */
public class Main {

    public static void main(String[] args) {
        try (Connection conexao = ConexaoBanco.conectar()) {

            TurmaDAO turmaDAO = new TurmaDAO(conexao);
            AlunoDAO alunoDAO = new AlunoDAO(conexao);
            ResponsavelDAO responsavelDAO = new ResponsavelDAO(conexao);
            RegistroSaidaDAO registroSaidaDAO = new RegistroSaidaDAO(conexao);

            // 1. Cadastro básico
            int turmaId = turmaDAO.inserir("2º Ano A");
            int alunoYasminId = alunoDAO.inserir("Yasmin Ferreira", turmaId);
            int alunoPedroId = alunoDAO.inserir("Pedro Souza", turmaId);

            // 2. Cadastro de responsáveis e vínculo com os alunos que podem retirar
            int responsavelId = responsavelDAO.inserir("Maria Ferreira", "123.456.789-00", null);
            responsavelDAO.vincularAluno(alunoYasminId, responsavelId);

            // 3. Simula o totem: responsável chega e seleciona a aluna Yasmin
            int registroId = registroSaidaDAO.chamarAluno(alunoYasminId, responsavelId);
            System.out.println("Yasmin foi chamada! (registro id=" + registroId + ")");

            // 4. Painel da sala antes da liberação do professor
            imprimirPainel(registroSaidaDAO, turmaId);

            // 5. Professor libera o aluno
            registroSaidaDAO.liberarAluno(registroId);
            System.out.println("\nProfessor liberou a Yasmin.");

            // 6. Painel da sala depois da liberação
            imprimirPainel(registroSaidaDAO, turmaId);

            // 7. Tentativa de retirada NÃO autorizada (Pedro não tem esse responsável vinculado)
            try {
                registroSaidaDAO.chamarAluno(alunoPedroId, responsavelId);
            } catch (SQLException e) {
                System.out.println("\nBloqueio esperado: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void imprimirPainel(RegistroSaidaDAO registroSaidaDAO, int turmaId) throws SQLException {
        System.out.println("\n--- Painel da sala (turma " + turmaId + ") ---");
        List<RegistroSaidaDAO.StatusAluno> status = registroSaidaDAO.statusPorTurma(turmaId);
        for (RegistroSaidaDAO.StatusAluno linha : status) {
            System.out.println(linha);
        }
    }
}