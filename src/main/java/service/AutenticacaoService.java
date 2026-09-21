package service;

import dao.UsuarioDAO;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;

public class AutenticacaoService {

    private final UsuarioDAO usuarioDAO;

    public AutenticacaoService(Connection conexao) {
        this.usuarioDAO = new UsuarioDAO(conexao);
    }

    /**
     * Cadastra um novo usuário. A senha nunca é guardada em texto puro — só
     * o hash BCrypt vai para o banco.
     *
     * turmaId é opcional (pode ser null): usado para "logins de sala" — uma
     * conta fixa vinculada a uma turma específica, que sempre mostra o
     * painel daquela sala, independente de quem estiver logado ali.
     * Contas de admin/secretaria, sem sala fixa, usam turmaId = null.
     */
    public int cadastrar(String nome, String email, String senha, String perfil, Integer turmaId)
            throws SQLException, RegraNegocioException {

        if (nome == null || nome.isBlank()) {
            throw new RegraNegocioException("Nome é obrigatório.");
        }
        if (email == null || email.isBlank()) {
            throw new RegraNegocioException("E-mail é obrigatório.");
        }
        if (senha == null || senha.length() < 6) {
            throw new RegraNegocioException("A senha precisa ter no mínimo 6 caracteres.");
        }
        if (!Usuario.PERFIL_ADMIN.equals(perfil)
                && !Usuario.PERFIL_SECRETARIA.equals(perfil)
                && !Usuario.PERFIL_SALA.equals(perfil)
                && !Usuario.PERFIL_TOTEM.equals(perfil)) {
            throw new RegraNegocioException("Perfil inválido. Use 'admin', 'secretaria', 'sala' ou 'totem'.");
        }
        if (usuarioDAO.existePorEmail(email)) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail.");
        }

        String senhaHash = BCrypt.hashpw(senha, BCrypt.gensalt());
        return usuarioDAO.inserir(nome.trim(), email.trim().toLowerCase(), senhaHash, perfil, turmaId);
    }

    /**
     * Autentica um usuário por e-mail e senha. Retorna o Usuario autenticado
     * ou lança RegraNegocioException se as credenciais forem inválidas.
     *
     * A mensagem de erro é propositalmente genérica (não diz se foi o e-mail
     * ou a senha que errou) — isso evita dar pistas a quem está tentando
     * adivinhar credenciais de outra pessoa.
     */
    public Usuario autenticar(String email, String senha) throws SQLException, RegraNegocioException {
        if (email == null || senha == null) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim().toLowerCase());
        if (usuario == null || !BCrypt.checkpw(senha, usuario.getSenhaHash())) {
            throw new RegraNegocioException("E-mail ou senha inválidos.");
        }

        return usuario;
    }

    public java.util.List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }
}