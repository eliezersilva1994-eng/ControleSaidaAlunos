package app;

import config.ConexaoBanco;
import model.Aluno;
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
    public ResponseEntity<?> criar(@RequestBody AlunoRequest requisicao) {
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
     * Lista os alunos de uma turma. Exemplo de uso:
     * GET /api/alunos?turmaId=1
     */
    @GetMapping
    public ResponseEntity<?> listarPorTurma(@RequestParam int turmaId) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            AlunoService alunoService = new AlunoService(conexao);
            List<Aluno> alunos = alunoService.listarPorTurma(turmaId);

            List<AlunoResponse> resposta = alunos.stream()
                    .map(a -> new AlunoResponse(a.getId(), a.getNome(), a.getTurmaId()))
                    .toList();
            return ResponseEntity.ok(resposta);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}