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
     * Insere o registro de chamada de um aluno pelo totem (sem validar regras de
     * negócio — isso é responsabilidade da camada service).
     */
    public int inserirChamada(int alunoId, int responsavelId) throws SQLException {
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
     * Busca o registro de saída mais recente de hoje para um aluno (ou null se
     * ele ainda não foi chamado hoje). Usado pelo Service para impedir chamadas
     * duplicadas ou chamar um aluno já liberado.
     */
    public RegistroSaida buscarUltimoDeHoje(int alunoId) throws SQLException {
        String sql = "SELECT id, aluno_id, responsavel_id, horario, status FROM registro_saida " +
                "WHERE aluno_id = ? AND horario::date = CURRENT_DATE " +
                "ORDER BY horario DESC LIMIT 1";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, alunoId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    private RegistroSaida mapear(ResultSet rs) throws SQLException {
        return new RegistroSaida(
                rs.getInt("id"),
                rs.getInt("aluno_id"),
                rs.getInt("responsavel_id"),
                rs.getTimestamp("horario"),
                rs.getString("status")
        );
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
                            status == null ? "aguardando" : status,
                            rs.getTimestamp("horario")
                    ));
                }
            }
        }
        return lista;
    }

    /**
     * Representação simples de linha do painel da sala: aluno + status atual
     * do dia + o horário em que foi chamado (null se ainda aguardando).
     */
    public static class StatusAluno {
        public final int alunoId;
        public final String nomeAluno;
        public final String status;
        public final java.sql.Timestamp horario;

        public StatusAluno(int alunoId, String nomeAluno, String status, java.sql.Timestamp horario) {
            this.alunoId = alunoId;
            this.nomeAluno = nomeAluno;
            this.status = status;
            this.horario = horario;
        }

        @Override
        public String toString() {
            return nomeAluno + " [" + status + "]";
        }
    }
}