package app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller de teste. Só serve para confirmar que o servidor web está
 * funcionando — acessando http://localhost:8080/api/status no navegador
 * deve responder com uma mensagem simples.
 */
@RestController
public class StatusController {

    @GetMapping("/api/status")
    public String status() {
        return "Sistema de Controle de Saída de Alunos está no ar!";
    }
}