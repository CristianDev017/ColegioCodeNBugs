package model.finanzas;

public class Solvencia {

    private int id;
    private int idEstudiante;
    private String carnet;
    private String estudiante;
    private int idCiclo;
    private int anio;
    private String grado;
    private String fechaEmision;
    private String usuario;

    public Solvencia(int id, int idEstudiante, String carnet, String estudiante, int idCiclo, int anio,
                     String grado, String fechaEmision, String usuario) {
        this.id = id;
        this.idEstudiante = idEstudiante;
        this.carnet = carnet;
        this.estudiante = estudiante;
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.grado = grado;
        this.fechaEmision = fechaEmision;
        this.usuario = usuario;
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

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public String getGrado() {
        return grado;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public String getUsuario() {
        return usuario;
    }
}