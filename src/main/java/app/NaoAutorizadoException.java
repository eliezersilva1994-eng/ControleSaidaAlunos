package app;

/**
 * Lançada quando uma requisição não tem um token válido (não autenticado)
 * ou tem um token válido mas o perfil não tem permissão para aquela ação
 * (não autorizado). O status HTTP correto (401 ou 403) vai junto.
 */
public class NaoAutorizadoException extends RuntimeException {

    private final int statusHttp;

    public NaoAutorizadoException(String mensagem, int statusHttp) {
        super(mensagem);
        this.statusHttp = statusHttp;
    }

    public int getStatusHttp() {
        return statusHttp;
    }
}