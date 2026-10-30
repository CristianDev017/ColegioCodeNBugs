package model.docencia;

public class ContextoAsignacion {

    private int idSeccion;
    private int idCiclo;
    private boolean cicloActivo;
    private String fechaInicio;
    private String fechaFin;

    public ContextoAsignacion(int idSeccion, int idCiclo, boolean cicloActivo, String fechaInicio, String fechaFin) {
        this.idSeccion = idSeccion;
        this.idCiclo = idCiclo;
        this.cicloActivo = cicloActivo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public boolean isCicloActivo() {
        return cicloActivo;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }
}