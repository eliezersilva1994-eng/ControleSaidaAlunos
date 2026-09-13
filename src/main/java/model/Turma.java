package model;

public class Turma {

    private int id;
    private String nome;

    public Turma() {
    }

    public Turma(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Turma(String nome) {
        this.nome = nome;
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

    @Override
    public String toString() {
        return "Turma{id=" + id + ", nome='" + nome + "'}";
    }
}