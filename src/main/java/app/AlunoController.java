package app;

import config.ConexaoBanco;
import jakarta.servlet.http.HttpServletRequest;
import model.Aluno;
import model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.AlunoService;
import service.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody AlunoRequest requisicao, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN);

        try (Connection conexao = ConexaoBanco.conectar()) {
            AlunoService alunoService = new AlunoService(conexao);
            int id = alunoService.inserir(requisicao.nome(), requisicao.turmaId());
            return ResponseEntity.ok(new AlunoResponse(id, requisicao.nome(), requisicao.turmaId()));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * GET /api/alunos?turmaId=1 — público: o totem precisa listar os alunos
     * de uma turma sem estar logado.
     * GET /api/alunos?nome=ana — protegido (admin/secretaria): é a busca
     * usada na tela de administração.
     */
    @GetMapping
    public ResponseEntity<?> listar(
            @RequestParam(required = false) Integer turmaId,
            @RequestParam(required = false) String nome,
            HttpServletRequest request) {

        try (Connection conexao = ConexaoBanco.conectar()) {
            AlunoService alunoService = new AlunoService(conexao);
            List<Aluno> alunos;

            if (nome != null && !nome.isBlank()) {
                ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);
                alunos = alunoService.buscarPorNome(nome);
            } else if (turmaId != null) {
                alunos = alunoService.listarPorTurma(turmaId);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErroResponse("Informe turmaId ou nome para buscar."));
            }

            List<AlunoResponse> resposta = alunos.stream()
                    .map(a -> new AlunoResponse(a.getId(), a.getNome(), a.getTurmaId()))
                    .toList();
            return ResponseEntity.ok(resposta);

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable int id, @RequestBody NomeRequest requisicao, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);

        try (Connection conexao = ConexaoBanco.conectar()) {
            AlunoService alunoService = new AlunoService(conexao);
            alunoService.atualizar(id, requisicao.nome());
            return ResponseEntity.ok(new MensagemResponse("Aluno atualizado com sucesso."));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable int id, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN);

        try (Connection conexao = ConexaoBanco.conectar()) {
            AlunoService alunoService = new AlunoService(conexao);
            alunoService.excluir(id);
            return ResponseEntity.ok(new MensagemResponse("Aluno excluído com sucesso."));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}