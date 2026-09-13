package dao;

import model.RegistroSaida;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RegistroSaidaDAO {

    private final Connection conexao;

    public RegistroSaidaDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Registra que um responsável chegou e "chamou" um aluno pelo totem.
     * Isso é o que muda o quadrado do aluno de cor na tela da sala.
     * Lança SQLException se o responsável não estiver autorizado a retirar o aluno.
     */
    public int chamarAluno(int alunoId, int responsavelId) throws SQLException {
        ResponsavelDAO responsavelDAO = new ResponsavelDAO(conexao);
        if (!responsavelDAO.estaAutorizado(alunoId, responsavelId)) {
            throw new SQLException("Responsável não autorizado a retirar este aluno.");
        }

        String sql = "INSERT INTO registro_saida (aluno_id, responsavel_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, alunoId);
            stmt.setInt(2, responsavelId);
            stmt.setString(3, RegistroSaida.STATUS_CHAMADO);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Falha ao registrar chamada, nenhum ID gerado.");
    }

    /**
     * O professor libera o aluno depois de ver o quadrado mudar de cor.
     */
    public void liberarAluno(int registroSaidaId) throws SQLException {
        String sql = "UPDATE registro_saida SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, RegistroSaida.STATUS_LIBERADO);
            stmt.setInt(2, registroSaidaId);
            stmt.executeUpdate();
        }
    }

    /**
     * Alimenta o painel em tempo real da sala: para cada aluno da turma,
     * mostra o status mais recente ("aguardando" se ainda não foi chamado hoje).
     */
    public List<StatusAluno> statusPorTurma(int turmaId) throws SQLException {
        String sql =
                "SELECT a.id AS aluno_id, a.nome, " +
                        "       rs.status, rs.horario " +
                        "FROM aluno a " +
                        "LEFT JOIN LATERAL ( " +
                        "    SELECT status, horario FROM registro_saida " +
                        "    WHERE aluno_id = a.id AND horario::date = CURRENT_DATE " +
                        "    ORDER BY horario DESC LIMIT 1 " +
                        ") rs ON true " +
                        "WHERE a.turma_id = ? " +
                        "ORDER BY a.nome";

        List<StatusAluno> lista = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, turmaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    lista.add(new StatusAluno(
                            rs.getInt("aluno_id"),
                            rs.getString("nome"),
                            status == null ? "aguardando" : status
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * Representação simples de linha do painel da sala: aluno + status atual do dia.
     */
    public static class StatusAluno {
        public final int alunoId;
        public final String nomeAluno;
        public final String status;

        public StatusAluno(int alunoId, String nomeAluno, String status) {
            this.alunoId = alunoId;
            this.nomeAluno = nomeAluno;
            this.status = status;
        }

        @Override
        public String toString() {
            return nomeAluno + " [" + status + "]";
        }
    }
}