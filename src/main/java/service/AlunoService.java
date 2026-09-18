package service;

import dao.AlunoDAO;
import model.Aluno;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AlunoService {

    private final AlunoDAO alunoDAO;

    public AlunoService(Connection conexao) {
        this.alunoDAO = new AlunoDAO(conexao);
    }

    public int inserir(String nome, int turmaId) throws SQLException, RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome do aluno é obrigatório.");
        }
        return alunoDAO.inserir(nome.trim(), turmaId);
    }

    public List<Aluno> listarPorTurma(int turmaId) throws SQLException {
        return alunoDAO.listarPorTurma(turmaId);
    }

    public List<Aluno> buscarPorNome(String nome) throws SQLException, RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Informe um nome para buscar.");
        }
        return alunoDAO.buscarPorNome(nome.trim());
    }

    public void atualizar(int id, String nome) throws SQLException, RegraNegocioException {
        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome do aluno é obrigatório.");
        }
        alunoDAO.atualizar(id, nome.trim());
    }

    public void excluir(int id) throws SQLException, RegraNegocioException {
        try {
            alunoDAO.excluir(id);
        } catch (SQLException e) {
            if ("23503".equals(e.getSQLState())) {
                throw new RegraNegocioException(
                        "Não é possível excluir: este aluno possui responsáveis vinculados ou registros de saída.");
            }
            throw e;
        }
    }
}