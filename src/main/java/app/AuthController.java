package app;

import config.ConexaoBanco;
import config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AutenticacaoService;
import service.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

    /**
     * Lista todos os usuários cadastrados. Só a diretora (admin) vê isso —
     * é informação sobre quem tem acesso ao sistema.
     */
    @GetMapping("/usuarios")
    public ResponseEntity<?> listar(HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN);

        try (Connection conexao = ConexaoBanco.conectar()) {
            AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);
            List<Usuario> usuarios = autenticacaoService.listarTodos();

            List<LoginResponse> resposta = usuarios.stream()
                    .map(u -> new LoginResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getTurmaId(), null))
                    .toList();
            return ResponseEntity.ok(resposta);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * Cria um novo login (admin, secretaria ou sala). Só a diretora (admin)
     * pode criar contas novas.
     */
    @PostMapping("/cadastrar-usuario")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroUsuarioRequest requisicao, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN);

        try (Connection conexao = ConexaoBanco.conectar()) {
            AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);
            int id = autenticacaoService.cadastrar(
                    requisicao.nome(), requisicao.email(), requisicao.senha(),
                    requisicao.perfil(), requisicao.turmaId());

            // token null: isto não é um login, é o admin criando a conta de
            // outra pessoa — não faz sentido devolver um crachá de acesso.
            LoginResponse resposta = new LoginResponse(
                    id, requisicao.nome(), requisicao.email(), requisicao.perfil(), requisicao.turmaId(), null);
            return ResponseEntity.ok(resposta);

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * Login: aberto a qualquer um que tenha e-mail/senha corretos — não
     * exige token, já que é aqui que o token é gerado.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            AutenticacaoService autenticacaoService = new AutenticacaoService(conexao);
            Usuario usuario = autenticacaoService.autenticar(requisicao.email(), requisicao.senha());

            String token = JwtUtil.gerarToken(usuario);

            LoginResponse resposta = new LoginResponse(
                    usuario.getId(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil(),
                    usuario.getTurmaId(), token);
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