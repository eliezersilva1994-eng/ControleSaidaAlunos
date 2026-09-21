package app;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Captura NaoAutorizadoException lançada em qualquer controller e devolve
 * a resposta HTTP certa (401 ou 403), sem cada controller precisar repetir
 * esse try/catch.
 */
@RestControllerAdvice
public class TratadorErrosGlobais {

    @ExceptionHandler(NaoAutorizadoException.class)
    public ResponseEntity<ErroResponse> tratarNaoAutorizado(NaoAutorizadoException e) {
        return ResponseEntity.status(HttpStatus.valueOf(e.getStatusHttp()))
                .body(new ErroResponse(e.getMessage()));
    }
}