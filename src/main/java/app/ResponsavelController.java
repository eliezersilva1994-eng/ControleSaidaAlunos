package app;

import config.ConexaoBanco;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RegraNegocioException;
import service.ResponsavelService;

import java.sql.Connection;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ResponsavelRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            int id = responsavelService.inserir(requisicao.nome(), requisicao.documento(), requisicao.fotoUrl());
            return ResponseEntity.ok(new ResponsavelResponse(id, requisicao.nome(), requisicao.documento()));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * Autoriza um responsável a retirar um aluno específico.
     */
    @PostMapping("/vincular")
    public ResponseEntity<?> vincular(@RequestBody VincularRequest requisicao) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            responsavelService.vincularAluno(requisicao.alunoId(), requisicao.responsavelId());
            return ResponseEntity.ok(new MensagemResponse("Responsável vinculado com sucesso."));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}