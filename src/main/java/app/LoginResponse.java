package app;

/**
 * Dados devolvidos após login bem-sucedido.
 * Note que NÃO inclui senha nem senha_hash — o cliente nunca deveria
 * receber esse dado de volta, mesmo com hash.
 */
public record LoginResponse(int id, String nome, String email, String perfil) {
}