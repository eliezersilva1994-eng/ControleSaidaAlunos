import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TesteBanco {

    public static void main(String[] args) {
        try (Connection conexao = ConexaoBanco.conectar()) {

            inserirTurma(conexao, "2º Ano A");
            inserirAluno(conexao, "Yasmin Ferreira", 1);
            inserirAluno(conexao, "Pedro Souza", 1);

            listarAlunos(conexao);

        } catch (SQLException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void inserirTurma(Connection conexao, String nome) throws SQLException {
        String sql = "INSERT INTO turma (nome) VALUES (?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();
            System.out.println("Turma inserida: " + nome);
        }
    }

    private static void inserirAluno(Connection conexao, String nome, int turmaId) throws SQLException {
        String sql = "INSERT INTO aluno (nome, turma_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setInt(2, turmaId);
            stmt.executeUpdate();
            System.out.println("Aluno inserido: " + nome);
        }
    }

    private static void listarAlunos(Connection conexao) throws SQLException {
        String sql = "SELECT a.nome, t.nome AS turma FROM aluno a JOIN turma t ON a.turma_id = t.id";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n--- Alunos cadastrados ---");
            while (rs.next()) {
                System.out.println(rs.getString("nome") + " - " + rs.getString("turma"));
            }
        }
    }
}