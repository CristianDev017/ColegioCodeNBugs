package model;

import java.math.BigDecimal;

public class Ponderacion {

    private int id;
    private String nombre;
    private BigDecimal porcentaje;
    private BigDecimal notaMaxima;

    public Ponderacion(int id, String nombre, BigDecimal porcentaje, BigDecimal notaMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.porcentaje = porcentaje;
        this.notaMaxima = notaMaxima;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }
}