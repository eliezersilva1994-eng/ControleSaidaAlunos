package dao;

import model.Responsavel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResponsavelDAO {

    private final Connection conexao;

    public ResponsavelDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public int inserir(String nome, String fotoUrl) throws SQLException {
        String sql = "INSERT INTO responsavel (nome, foto_url) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, fotoUrl);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao inserir responsável, nenhum ID gerado.");
    }

    public void vincularAluno(int alunoId, int responsavelId) throws SQLException {
        String sql = "INSERT INTO aluno_responsavel (aluno_id, responsavel_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            stmt.setInt(2, responsavelId);
            stmt.executeUpdate();
        }
    }

    /**
     * Remove a autorização de um responsável para retirar um aluno.
     */
    public void desvincular(int alunoId, int responsavelId) throws SQLException {
        String sql = "DELETE FROM aluno_responsavel WHERE aluno_id = ? AND responsavel_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            stmt.setInt(2, responsavelId);
            stmt.executeUpdate();
        }
    }

    public boolean estaAutorizado(int alunoId, int responsavelId) throws SQLException {
        String sql = "SELECT 1 FROM aluno_responsavel WHERE aluno_id = ? AND responsavel_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            stmt.setInt(2, responsavelId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Responsavel> listarTodos() throws SQLException {
        String sql = "SELECT id, nome, foto_url FROM responsavel ORDER BY nome";
        List<Responsavel> responsaveis = new ArrayList<>();

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                responsaveis.add(new Responsavel(
                        rs.getInt("id"), rs.getString("nome"), rs.getString("foto_url")));
            }
        }
        return responsaveis;
    }

    public List<Responsavel> listarPorAluno(int alunoId) throws SQLException {
        String sql = "SELECT r.id, r.nome, r.foto_url " +
                "FROM responsavel r " +
                "JOIN aluno_responsavel ar ON ar.responsavel_id = r.id " +
                "WHERE ar.aluno_id = ? " +
                "ORDER BY r.nome";
        List<Responsavel> responsaveis = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    responsaveis.add(new Responsavel(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("foto_url")
                    ));
                }
            }
        }
        return responsaveis;
    }

    public void atualizar(int id, String nome, String fotoUrl) throws SQLException {
        String sql = "UPDATE responsavel SET nome = ?, foto_url = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.setString(2, fotoUrl);
            stmt.setInt(3, id);
            stmt.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM responsavel WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}