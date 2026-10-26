package model;

import java.math.BigDecimal;

public class Actividad {

    private int id;
    private String nombre;
    private BigDecimal costo;
    private int cupoTotal;
    private int inscritos;
    private int disponibles;
    private boolean activo;

    public Actividad(int id, String nombre, BigDecimal costo, int cupoTotal, int inscritos, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.costo = costo;
        this.cupoTotal = cupoTotal;
        this.inscritos = inscritos;
        this.disponibles = cupoTotal - inscritos;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public int getCupoTotal() {
        return cupoTotal;
    }

    public int getInscritos() {
        return inscritos;
    }

    public boolean isActivo() {
        return activo;
    }
}