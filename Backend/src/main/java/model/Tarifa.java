package model;

import java.math.BigDecimal;

public class Tarifa {

    private int id;
    private int idGrado;
    private String grado;
    private int idCiclo;
    private int anio;
    private BigDecimal montoInscripcion;
    private BigDecimal montoMensualidad;
    private int diaVencimiento;

    public Tarifa(int id, int idGrado, String grado, int idCiclo, int anio,
                  BigDecimal montoInscripcion, BigDecimal montoMensualidad, int diaVencimiento) {
        this.id = id;
        this.idGrado = idGrado;
        this.grado = grado;
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.montoInscripcion = montoInscripcion;
        this.montoMensualidad = montoMensualidad;
        this.diaVencimiento = diaVencimiento;
    }

    public int getId() {
        return id;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public String getGrado() {
        return grado;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public BigDecimal getMontoInscripcion() {
        return montoInscripcion;
    }

    public BigDecimal getMontoMensualidad() {
        return montoMensualidad;
    }

    public int getDiaVencimiento() {
        return diaVencimiento;
    }
}