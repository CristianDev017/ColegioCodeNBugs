package model.docencia;

import java.math.BigDecimal;

public class AlumnoNota {

    private int idInscripcion;
    private String carnet;
    private String estudiante;
    private BigDecimal valor;

    public AlumnoNota(int idInscripcion, String carnet, String estudiante, BigDecimal valor) {
        this.idInscripcion = idInscripcion;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.valor = valor;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public String getCarnet() {
        return carnet;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public BigDecimal getValor() {
        return valor;
    }
}