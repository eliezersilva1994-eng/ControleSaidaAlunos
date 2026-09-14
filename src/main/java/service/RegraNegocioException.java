package service;

/**
 * Lançada quando uma regra de negócio é violada (ex: responsável não
 * autorizado, aluno já liberado hoje). Diferente de SQLException, que
 * representa um problema técnico de banco de dados.
 */
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String message) {
        super(message);
    }
}