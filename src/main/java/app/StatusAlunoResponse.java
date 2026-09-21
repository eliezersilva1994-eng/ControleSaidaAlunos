package app;

/**
 * horario: momento em que o aluno foi chamado (null se ainda aguardando).
 * Formato ISO-8601 (ex: "2026-09-21T16:45:12.000+00:00"), convertido
 * automaticamente pelo Spring a partir do Timestamp do banco.
 */
public record StatusAlunoResponse(int alunoId, String nomeAluno, String status, java.sql.Timestamp horario) {
}