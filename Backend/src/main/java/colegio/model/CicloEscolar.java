package colegio.model;

import java.sql.Date;

public class CicloEscolar {

    private int idCiclo;
    private int anio;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado;
    private int cantidadGrados;

    public CicloEscolar(int idCiclo, int anio, Date fechaInicio,
                        Date fechaFin, String estado, int cantidadGrados) {
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.cantidadGrados = cantidadGrados;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public int getCantidadGrados() {
        return cantidadGrados;
    }
}
