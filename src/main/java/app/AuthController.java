package app;

import config.ConexaoBanco;
import model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.AutenticacaoService;
import service.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Expõe a autenticação (login) como um endpoint HTTP.
 *
 * Cada requisição abre sua própria conexão com o banco e fecha em
 * seguida (try-with-resources) — é o mesmo padrão que já usávamos no
 * Main.java, só que agora disparado por uma requisição web em vez de
 * rodar uma vez só no início do programa.
 */
@RestController
@RequestMapping("/api")
public class AuthController {

    @PostMapping("/cadastrar-usuario")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroUsuarioRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);
            int id = autenticacaoService.cadastrar(
                    requisicao.nome(), requisicao.email(), requisicao.senha(),
                    requisicao.perfil(), requisicao.turmaId());

            LoginResponse resposta = new LoginResponse(
                    id, requisicao.nome(), requisicao.email(), requisicao.perfil(), requisicao.turmaId());
            return ResponseEntity.ok(resposta);

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);
            Usuario usuario = autenticacaoService.autenticar(requisicao.email(), requisicao.senha());

            LoginResponse resposta = new LoginResponse(
                    usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil(), usuario.getTurmaId());
            return ResponseEntity.ok(resposta);

        } catch (RegraNegocioException e) {
            // Credenciais inválidas -> 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            // Problema técnico de banco -> 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}