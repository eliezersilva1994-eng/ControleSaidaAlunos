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
}