package app;

/**
 * turmaId é opcional (pode vir null ou ausente no JSON) — usado para logins
 * de sala. Deixe de fora para contas sem turma fixa (ex: admin/secretaria).
 */
public record CadastroUsuarioRequest(String nome, String email, String senha, String perfil, Integer turmaId) {
}