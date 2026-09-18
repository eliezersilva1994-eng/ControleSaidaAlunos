package dao;

import model.Aluno;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    private final Connection conexao;

    public AlunoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public int inserir(String nome, int turmaId) throws SQLException {
        String sql = "INSERT INTO aluno (nome, turma_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setInt(2, turmaId);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao inserir aluno, nenhum ID gerado.");
    }

    public List<Aluno> listarPorTurma(int turmaId) throws SQLException {
        String sql = "SELECT id, nome, turma_id FROM aluno WHERE turma_id = ? ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, turmaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alunos.add(new Aluno(rs.getInt("id"), rs.getString("nome"), rs.getInt("turma_id")));
                }
            }
        }
        return alunos;
    }

    public List<Aluno> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, turma_id FROM aluno ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                alunos.add(new Aluno(rs.getInt("id"), rs.getString("nome"), rs.getInt("turma_id")));
            }
        }
        return alunos;
    }

    /**
     * Busca alunos cujo nome contém o texto informado (sem diferenciar
     * maiúsculas/minúsculas), em qualquer turma. Usado na busca da tela de
     * administração.
     */
    public List<Aluno> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT id, nome, turma_id FROM aluno WHERE nome ILIKE ? ORDER BY nome";
        List<Aluno> alunos = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    alunos.add(new Aluno(rs.getInt("id"), rs.getString("nome"), rs.getInt("turma_id")));
                }
            }
        }
        return alunos;
    }

    public void atualizar(int id, String nome) throws SQLException {
        String sql = "UPDATE aluno SET nome = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM aluno WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}