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
     * 2) o aluno não pode já ter sido chamado hoje (não há como o sistema
     *    validar se ele "já foi entregue" — uma vez chamado, o professor
     *    manda o aluno para a porta e o fluxo daquele aluno termina ali).
     */
    public int chamarAluno(int alunoId, int responsavelId) throws SQLException, RegraNegocioException {
        if (!responsavelDAO.estaAutorizado(alunoId, responsavelId)) {
            throw new RegraNegocioException("Responsável não autorizado a retirar este aluno.");
        }

        RegistroSaida ultimo = registroSaidaDAO.buscarUltimoDeHoje(alunoId);
        if (ultimo != null) {
            throw new RegraNegocioException("Aluno já foi chamado hoje.");
        }

        return registroSaidaDAO.inserirChamada(alunoId, responsavelId);
    }

    /**
     * Alimenta o painel em tempo real da sala de aula.
     */
    public List<RegistroSaidaDAO.StatusAluno> statusPorTurma(int turmaId) throws SQLException {
        return registroSaidaDAO.statusPorTurma(turmaId);
    }
}