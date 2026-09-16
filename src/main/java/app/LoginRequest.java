package app;

/**
 * Dados que o cliente envia para fazer login.
 * Um "record" é um jeito enxuto do Java de representar dados simples
 * (equivalente a uma classe só com campos finais + getters).
 */
public record LoginRequest(String email, String senha) {
}