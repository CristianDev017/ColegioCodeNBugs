package model;

public class CicloEscolar {

    private int id;
    private int anio;
    private String fechaInicio;
    private String fechaFin;
    private String estado;

    public CicloEscolar(int id, int anio, String fechaInicio, String fechaFin, String estado) {
        this.id = id;
        this.anio = anio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getAnio() {
        return anio;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getEstado() {
        return estado;
    }
}
