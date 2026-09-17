package app;

import config.ConexaoBanco;
import dao.RegistroSaidaDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RegistroSaidaService;
import service.RegraNegocioException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * O coração do sistema: chamar um aluno pelo totem, e o painel da sala
 * consultando o status em tempo real.
 */
@RestController
@RequestMapping("/api/registros")
public class RegistroSaidaController {

    /**
     * Totem: o responsável seleciona o aluno.
     */
    @PostMapping("/chamar")
    public ResponseEntity<?> chamar(@RequestBody ChamarAlunoRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            RegistroSaidaService registroSaidaService = new RegistroSaidaService(conexao);
            int registroId = registroSaidaService.chamarAluno(requisicao.alunoId(), requisicao.responsavelId());
            return ResponseEntity.ok(new ChamarAlunoResponse(registroId));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * Painel da sala: consulta o status atual de todos os alunos da turma.
     * O painel deve chamar isso a cada poucos segundos (polling).
     */
    @GetMapping("/status")
    public ResponseEntity<?> statusPorTurma(@RequestParam int turmaId) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            RegistroSaidaService registroSaidaService = new RegistroSaidaService(conexao);
            List<RegistroSaidaDAO.StatusAluno> status = registroSaidaService.statusPorTurma(turmaId);

            List<StatusAlunoResponse> resposta = status.stream()
                    .map(s -> new StatusAlunoResponse(s.alunoId, s.nomeAluno, s.status))
                    .toList();
            return ResponseEntity.ok(resposta);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}