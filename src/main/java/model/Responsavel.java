package model;

public class Responsavel {

    private int id;
    private String nome;
    private String documento;
    private String fotoUrl;

    public Responsavel() {
    }

    public Responsavel(int id, String nome, String documento, String fotoUrl) {
        this.id = id;
        this.nome = nome;
        this.documento = documento;
        this.fotoUrl = fotoUrl;
    }

    public Responsavel(String nome, String documento, String fotoUrl) {
        this.nome = nome;
        this.documento = documento;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    @Override
    public String toString() {
        return "Responsavel{id=" + id + ", nome='" + nome + "', documento='" + documento + "'}";
    }
}