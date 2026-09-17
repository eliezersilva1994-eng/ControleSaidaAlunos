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
}