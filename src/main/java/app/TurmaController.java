package app;

import config.ConexaoBanco;
import model.Turma;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RegraNegocioException;
import service.TurmaService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/turmas")
public class TurmaController {

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody TurmaRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            TurmaService turmaService = new TurmaService(conexao);
            int id = turmaService.inserir(requisicao.nome());
            return ResponseEntity.ok(new TurmaResponse(id, requisicao.nome()));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try (Connection conexao = ConexaoBanco.conectar()) {
            TurmaService turmaService = new TurmaService(conexao);
            List<Turma> turmas = turmaService.listarTodas();

            List<TurmaResponse> resposta = turmas.stream()
                    .map(t -> new TurmaResponse(t.getId(), t.getNome()))
                    .toList();
            return ResponseEntity.ok(resposta);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable int id, @RequestBody NomeRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            TurmaService turmaService = new TurmaService(conexao);
            turmaService.atualizar(id, requisicao.nome());
            return ResponseEntity.ok(new MensagemResponse("Turma atualizada com sucesso."));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> excluir(@PathVariable int id) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            TurmaService turmaService = new TurmaService(conexao);
            turmaService.excluir(id);
            return ResponseEntity.ok(new MensagemResponse("Turma excluída com sucesso."));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}