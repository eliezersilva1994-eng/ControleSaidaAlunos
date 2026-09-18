package model;

public class Responsavel {

    private int id;
    private String nome;
    private String fotoUrl;

    public Responsavel() {
    }

    public Responsavel(int id, String nome, String fotoUrl) {
        this.id = id;
        this.nome = nome;
        this.fotoUrl = fotoUrl;
    }

    public Responsavel(String nome, String fotoUrl) {
        this.nome = nome;
        this.fotoUrl = fotoUrl;
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

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return "Responsavel{id=" + id + ", nome='" + nome + "'}";
    }
}