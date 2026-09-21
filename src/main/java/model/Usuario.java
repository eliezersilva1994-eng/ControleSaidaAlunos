package model;

public class Usuario {

    // Perfis de acesso do sistema:
    // - admin: acesso total (diretora) — cria/edita/exclui tudo
    // - secretaria: acesso limitado — cadastra responsáveis, lista e edita
    //   alunos, autoriza/remove retiradas; não gerencia turmas, não exclui,
    //   não cria logins novos
    // - sala: login fixo de uma turma (a TV), sempre com turmaId preenchido,
    //   vai direto para o painel daquela sala, sem acesso à administração
    // - totem: login do tablet da portaria — sem ele, ninguém consegue
    //   chamar aluno nem ver quais responsáveis são autorizados
    public static final String PERFIL_ADMIN = "admin";
    public static final String PERFIL_SECRETARIA = "secretaria";
    public static final String PERFIL_SALA = "sala";
    public static final String PERFIL_TOTEM = "totem";

    private int id;
    private String nome;
    private String email;
    private String senhaHash;
    private String perfil;
    private Integer turmaId; // null = login sem turma fixa (ex: admin/secretaria)

    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String senhaHash, String perfil, Integer turmaId) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
        this.turmaId = turmaId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public Integer getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(Integer turmaId) {
        this.turmaId = turmaId;
    }

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "', perfil='" + perfil
                + "', turmaId=" + turmaId + "}";
    }
}