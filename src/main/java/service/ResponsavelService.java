package service;

import dao.ResponsavelDAO;
import model.Responsavel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ResponsavelService {

    private final ResponsavelDAO responsavelDAO;

    public ResponsavelService(Connection conexao) {
        this.responsavelDAO = new ResponsavelDAO(conexao);
    }

    public int inserir(String nome, String fotoUrl) throws SQLException, RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome do responsável é obrigatório.");
        }
        return responsavelDAO.inserir(nome.trim(), fotoUrl);
    }

    /**
     * Autoriza um responsável a retirar um aluno. Bloqueia vínculo duplicado.
     */
    public void vincularAluno(int alunoId, int responsavelId) throws SQLException, RegraNegocioException {
        if (responsavelDAO.estaAutorizado(alunoId, responsavelId)) {
            throw new RegraNegocioException("Este responsável já está vinculado a este aluno.");
        }
        responsavelDAO.vincularAluno(alunoId, responsavelId);
    }

    public List<Responsavel> listarTodos() throws SQLException {
        return responsavelDAO.listarTodos();
    }

    public List<Responsavel> listarPorAluno(int alunoId) throws SQLException {
        return responsavelDAO.listarPorAluno(alunoId);
    }
}