package model;

import java.sql.Timestamp;

public class RegistroSaida {

    // Único status possível hoje: o aluno foi chamado pelo totem. Depois
    // disso, o fluxo daquele aluno termina — não há confirmação digital de
    // entrega, o professor apenas manda o aluno para a porta.
    public static final String STATUS_CHAMADO = "chamado";

    private int id;
    private int alunoId;
    private int responsavelId;
    private Timestamp horario;
    private String status;

    public RegistroSaida() {
    }

    public RegistroSaida(int id, int alunoId, int responsavelId, Timestamp horario, String status) {
        this.id = id;
        this.alunoId = alunoId;
        this.responsavelId = responsavelId;
        this.horario = horario;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(int alunoId) {
        this.alunoId = alunoId;
    }

    public int getResponsavelId() {
        return responsavelId;
    }

    public void setResponsavelId(int responsavelId) {
        this.responsavelId = responsavelId;
    }

    public Timestamp getHorario() {
        return horario;
    }

    public void setHorario(Timestamp horario) {
        this.horario = horario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "RegistroSaida{id=" + id + ", alunoId=" + alunoId + ", responsavelId=" + responsavelId
                + ", horario=" + horario + ", status='" + status + "'}";
    }
}