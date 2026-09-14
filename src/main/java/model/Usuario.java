package model;

public class Usuario {

    public static final String PERFIL_PROFESSOR = "professor";
    public static final String PERFIL_ADMIN = "admin";

    private int id;
    private String nome;
    private String email;
    private String senhaHash;
    private String perfil;

    public Usuario() {
    }

    public Usuario(int id, String nome, String email, String senhaHash, String perfil) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
        this.perfil = perfil;
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

    @Override
    public String toString() {
        return "Usuario{id=" + id + ", nome='" + nome + "', email='" + email + "', perfil='" + perfil + "'}";
    }
}