package app;

/**
 * Dados de quem está autenticado na requisição atual, extraídos do token.
 */
public record UsuarioAutenticado(int id, String email, String perfil, Integer turmaId) {
}