package service;

import dao.RegistroSaidaDAO;
import dao.ResponsavelDAO;
import model.RegistroSaida;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RegistroSaidaService {

    private final RegistroSaidaDAO registroSaidaDAO;
    private final ResponsavelDAO responsavelDAO;

    public RegistroSaidaService(Connection conexao) {
        this.registroSaidaDAO = new RegistroSaidaDAO(conexao);
        this.responsavelDAO = new ResponsavelDAO(conexao);
    }

    /**
     * Chama um aluno pelo totem. Regras aplicadas, nessa ordem:
     * 1) o responsável precisa estar autorizado a retirar o aluno;
     * 2) o aluno não pode já estar "chamado" (aguardando liberação);
     * 3) o aluno não pode já ter sido "liberado" hoje.
     */
    public int chamarAluno(int alunoId, int responsavelId) throws SQLException, RegraNegocioException {
        if (!responsavelDAO.estaAutorizado(alunoId, responsavelId)) {
            throw new RegraNegocioException("Responsável não autorizado a retirar este aluno.");
        }

        RegistroSaida ultimo = registroSaidaDAO.buscarUltimoDeHoje(alunoId);
        if (ultimo != null) {
            if (RegistroSaida.STATUS_LIBERADO.equals(ultimo.getStatus())) {
                throw new RegraNegocioException("Aluno já foi liberado hoje.");
            }
            if (RegistroSaida.STATUS_CHAMADO.equals(ultimo.getStatus())) {
                throw new RegraNegocioException("Aluno já foi chamado e aguarda liberação do professor.");
            }
        }

        return registroSaidaDAO.inserirChamada(alunoId, responsavelId);
    }

    /**
     * Professor libera o aluno. Só permite liberar um registro que exista e
     * ainda não tenha sido liberado.
     */
    public void liberarAluno(int registroSaidaId) throws SQLException, RegraNegocioException {
        RegistroSaida registro = registroSaidaDAO.buscarPorId(registroSaidaId);
        if (registro == null) {
            throw new RegraNegocioException("Registro de saída não encontrado.");
        }
        if (RegistroSaida.STATUS_LIBERADO.equals(registro.getStatus())) {
            throw new RegraNegocioException("Este aluno já está liberado.");
        }
        registroSaidaDAO.liberarAluno(registroSaidaId);
    }

    /**
     * Alimenta o painel em tempo real da sala de aula.
     */
    public List<RegistroSaidaDAO.StatusAluno> statusPorTurma(int turmaId) throws SQLException {
        return registroSaidaDAO.statusPorTurma(turmaId);
    }
}