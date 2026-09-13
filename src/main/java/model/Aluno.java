package model;

public class Aluno {

    private int id;
    private String nome;
    private int turmaId;

    public Aluno() {
    }

    public Aluno(int id, String nome, int turmaId) {
        this.id = id;
        this.nome = nome;
        this.turmaId = turmaId;
    }

    public Aluno(String nome, int turmaId) {
        this.nome = nome;
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

    public int getTurmaId() {
        return turmaId;
    }

    public void setTurmaId(int turmaId) {
        this.turmaId = turmaId;
    }

    @Override
    public String toString() {
        return "Aluno{id=" + id + ", nome='" + nome + "', turmaId=" + turmaId + "}";
    }
}