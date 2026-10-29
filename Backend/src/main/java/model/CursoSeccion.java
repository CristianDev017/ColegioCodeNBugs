package model;

public class CursoSeccion {

    private int idCurriculoCurso;
    private String curso;
    private Integer idAsignacion;
    private Integer idEmpleado;
    private String maestro;

    public CursoSeccion(int idCurriculoCurso, String curso, Integer idAsignacion, Integer idEmpleado, String maestro) {
        this.idCurriculoCurso = idCurriculoCurso;
        this.curso = curso;
        this.idAsignacion = idAsignacion;
        this.idEmpleado = idEmpleado;
        this.maestro = maestro;
    }

    public int getIdCurriculoCurso() {
        return idCurriculoCurso;
    }

    public String getCurso() {
        return curso;
    }

    public Integer getIdAsignacion() {
        return idAsignacion;
    }

    public Integer getIdEmpleado() {
        return idEmpleado;
    }

    public String getMaestro() {
        return maestro;
    }
}