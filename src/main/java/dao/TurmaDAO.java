package dao;

import model.Turma;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TurmaDAO {

    private final Connection conexao;

    public TurmaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public int inserir(String nome) throws SQLException {
        String sql = "INSERT INTO turma (nome) VALUES (?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao inserir turma, nenhum ID gerado.");
    }

    public List<Turma> listarTodas() throws SQLException {
        String sql = "SELECT id, nome FROM turma ORDER BY nome";
        List<Turma> turmas = new ArrayList<>();

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                turmas.add(new Turma(rs.getInt("id"), rs.getString("nome")));
            }
        }
        return turmas;
    }

    /**
     * Verifica se já existe uma turma com esse nome (comparação exata).
     */
    public boolean existePorNome(String nome) throws SQLException {
        String sql = "SELECT 1 FROM turma WHERE nome = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nome);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}