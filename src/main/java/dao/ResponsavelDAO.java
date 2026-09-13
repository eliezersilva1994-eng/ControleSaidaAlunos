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

    public int inserir(String nome, String documento, String fotoUrl) throws SQLException {
        String sql = "INSERT INTO responsavel (nome, documento, foto_url) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, documento);
            stmt.setString(3, fotoUrl);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao inserir responsável, nenhum ID gerado.");
    }

    /**
     * Vincula um responsável a um aluno, autorizando-o a retirá-lo.
     * Um aluno pode ter vários responsáveis autorizados.
     */
    public void vincularAluno(int alunoId, int responsavelId) throws SQLException {
        String sql = "INSERT INTO aluno_responsavel (aluno_id, responsavel_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            stmt.setInt(2, responsavelId);
            stmt.executeUpdate();
        }
    }

    /**
     * Verifica se um responsável está autorizado a retirar um determinado aluno.
     * Essa é a validação central de segurança do sistema.
     */
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

    /**
     * Lista todos os responsáveis autorizados a retirar um aluno específico.
     * Útil para a tela de totem: o responsável se identifica e o sistema
     * confirma se ele está na lista antes de liberar a seleção.
     */
    public List<Responsavel> listarPorAluno(int alunoId) throws SQLException {
        String sql = "SELECT r.id, r.nome, r.documento, r.foto_url " +
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
                            rs.getString("documento"),
                            rs.getString("foto_url")
                    ));
                }
            }
        }
        return responsaveis;
    }
}