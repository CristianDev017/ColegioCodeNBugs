package model.estudiantes;

public class InscripcionDetalle {

    private int id;
    private int idEstudiante;
    private String carnet;
    private String estudiante;
    private int idSeccion;
    private String grado;
    private String seccion;
    private int anio;
    private String fechaInscripcion;
    private String estado;

    public InscripcionDetalle(int id, int idEstudiante, String carnet, String estudiante, int idSeccion,
                              String grado, String seccion, int anio, String fechaInscripcion, String estado) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.idSeccion = idSeccion;
        this.grado = grado;
        this.seccion = seccion;
        this.anio = anio;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public String getCarnet() {
        return carnet;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public String getGrado() {
        return grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getAnio() {
        return anio;
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }
}