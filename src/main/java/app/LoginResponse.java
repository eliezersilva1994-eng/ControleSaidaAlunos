package app;

/**
 * Dados devolvidos após login bem-sucedido.
 * Note que NÃO inclui senha nem senha_hash — o cliente nunca deveria
 * receber esse dado de volta, mesmo com hash.
 *
 * turmaId: se não for null, é um "login de sala" — a tela deve ir direto
 * para o painel dessa turma, sem pedir pra escolher. Se for null, é uma
 * conta sem sala fixa (ex: admin/secretaria).
 */
public record LoginResponse(int id, String nome, String email, String perfil, Integer turmaId) {
}