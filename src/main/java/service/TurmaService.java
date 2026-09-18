package service;

import dao.TurmaDAO;
import model.Turma;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TurmaService {

    private final TurmaDAO turmaDAO;

    public TurmaService(Connection conexao) {
        this.turmaDAO = new TurmaDAO(conexao);
    }

    public int inserir(String nome) throws SQLException, RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome da turma é obrigatório.");
        }

        String nomeLimpo = nome.trim();
        if (turmaDAO.existePorNome(nomeLimpo)) {
            throw new RegraNegocioException("Já existe uma turma com esse nome.");
        }

        return turmaDAO.inserir(nomeLimpo);
    }

    public List<Turma> listarTodas() throws SQLException {
        return turmaDAO.listarTodas();
    }

    public void atualizar(int id, String nome) throws SQLException, RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome da turma é obrigatório.");
        }

        String nomeLimpo = nome.trim();
        if (turmaDAO.existePorNomeExcetoId(nomeLimpo, id)) {
            throw new RegraNegocioException("Já existe outra turma com esse nome.");
        }

        turmaDAO.atualizar(id, nomeLimpo);
    }

    public void excluir(int id) throws SQLException, RegraNegocioException {
        try {
            turmaDAO.excluir(id);
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new RegraNegocioException(
                        "Não é possível excluir: existem alunos cadastrados nesta turma.");
            }
            throw e;
        }
    }
}