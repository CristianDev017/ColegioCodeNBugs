package model.docencia;

public class CursoMaestro {

    private int idAsignacion;
    private String curso;
    private String seccion;
    private int idCiclo;
    private int anio;
    private int estudiantes;

    public CursoMaestro(int idAsignacion, String curso, String seccion, int idCiclo, int anio, int estudiantes) {
        this.idAsignacion = idAsignacion;
        this.curso = curso;
        this.seccion = seccion;
        this.idCiclo = idCiclo;
        this.anio = anio;
        this.estudiantes = estudiantes;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public String getCurso() {
        return curso;
    }

    public String getSeccion() {
        return seccion;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getAnio() {
        return anio;
    }

    public int getEstudiantes() {
        return estudiantes;
    }
}