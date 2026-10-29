package model;

public class AlumnoNomina {

    private int idInscripcion;
    private String carnet;
    private String estudiante;
    private String estado;

    public AlumnoNomina(int idInscripcion, String carnet, String estudiante, String estado) {
        this.idInscripcion = idInscripcion;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.estado = estado;
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

    public String getEstado() {
        return estado;
    }
}