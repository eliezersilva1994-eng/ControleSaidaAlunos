package dao;

import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class UsuarioDAO {

    private final Connection conexao;

    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * turmaId pode ser null (ex: contas de administração/secretaria, que não
     * pertencem a uma sala específica).
     */
    public int inserir(String nome, String email, String senhaHash, String perfil, Integer turmaId) throws SQLException {
        String sql = "INSERT INTO usuario (nome, email, senha_hash, perfil, turma_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senhaHash);
            stmt.setString(4, perfil);
            if (turmaId == null) {
                stmt.setNull(5, Types.INTEGER);
            } else {
                stmt.setInt(5, turmaId);
            }
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao inserir usuário, nenhum ID gerado.");
    }

    /**
     * Busca um usuário pelo e-mail (usado no login). Retorna null se não existir.
     */
    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT id, nome, email, senha_hash, perfil, turma_id FROM usuario WHERE email = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                            rs.getInt("id"),
                            rs.getString("nome"),
                            rs.getString("email"),
                            rs.getString("senha_hash"),
                            rs.getString("perfil"),
                            rs.getObject("turma_id", Integer.class)
                    );
                }
            }
        }
        return null;
    }

    /**
     * Verifica se já existe um usuário cadastrado com esse e-mail.
     */
    public boolean existePorEmail(String email) throws SQLException {
        return buscarPorEmail(email) != null;
    }
}