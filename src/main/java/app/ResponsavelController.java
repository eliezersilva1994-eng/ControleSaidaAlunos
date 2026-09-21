package app;

import config.ConexaoBanco;
import jakarta.servlet.http.HttpServletRequest;
import model.Responsavel;
import model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.RegraNegocioException;
import service.ResponsavelService;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/responsaveis")
public class ResponsavelController {

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody ResponsavelRequest requisicao, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);

        try (Connection conexao = ConexaoBanco.conectar()) {
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            int id = responsavelService.inserir(requisicao.nome(), requisicao.fotoUrl());
            return ResponseEntity.ok(new ResponsavelResponse(id, requisicao.nome()));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * Com alunoId: público (o totem mostra os responsáveis autorizados de
     * um aluno, sem login). Sem alunoId (lista todos): protegido, usado na
     * tela de administração.
     */
    @GetMapping
    public ResponseEntity<?> listar(@RequestParam(required = false) Integer alunoId, HttpServletRequest request) {
        try (Connection conexao = ConexaoBanco.conectar()) {
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            List<Responsavel> responsaveis;

            if (alunoId == null) {
                ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);
                responsaveis = responsavelService.listarTodos();
            } else {
                responsaveis = responsavelService.listarPorAluno(alunoId);
            }

            List<ResponsavelResponse> resposta = responsaveis.stream()
                    .map(r -> new ResponsavelResponse(r.getId(), r.getNome()))
                    .toList();
            return ResponseEntity.ok(resposta);

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    /**
     * Autoriza um responsável a retirar um aluno específico.
     */
    @PostMapping("/vincular")
    public ResponseEntity<?> vincular(@RequestBody VincularRequest requisicao, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);

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

    /**
     * Remove a autorização de um responsável para retirar um aluno.
     */
    @DeleteMapping("/vincular")
    public ResponseEntity<?> desvincular(@RequestParam int alunoId, @RequestParam int responsavelId, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN, Usuario.PERFIL_SECRETARIA);

        try (Connection conexao = ConexaoBanco.conectar()) {
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            responsavelService.desvincularAluno(alunoId, responsavelId);
            return ResponseEntity.ok(new MensagemResponse("Vínculo removido com sucesso."));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable int id, @RequestBody ResponsavelRequest requisicao, HttpServletRequest request) {
        ContextoAutenticacao.exigirPerfil(request, Usuario.PERFIL_ADMIN);

        try (Connection conexao = ConexaoBanco.conectar()) {
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            responsavelService.atualizar(id, requisicao.nome(), requisicao.fotoUrl());
            return ResponseEntity.ok(new MensagemResponse("Responsável atualizado com sucesso."));

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
            ResponsavelService responsavelService = new ResponsavelService(conexao);
            responsavelService.excluir(id);
            return ResponseEntity.ok(new MensagemResponse("Responsável excluído com sucesso."));

        } catch (RegraNegocioException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErroResponse(e.getMessage()));

        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErroResponse("Erro de banco de dados: " + e.getMessage()));
        }
    }
}