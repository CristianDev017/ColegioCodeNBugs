package model;

public class Asignacion {

    private int id;
    private int idEmpleado;
    private String maestro;
    private int idCurriculoCurso;
    private String curso;
    private int idSeccion;
    private String seccion;
    private int idCiclo;
    private int anio;

    public Asignacion(int id, int idEmpleado, String maestro, int idCurriculoCurso, String curso,
                      int idSeccion, String seccion, int idCiclo, int anio) {
        this.id = id;
        this.idEmpleado = idEmpleado;
        this.maestro = maestro;
        this.idCurriculoCurso = idCurriculoCurso;
        this.curso = curso;
        this.idSeccion = idSeccion;
        this.seccion = seccion;
        this.idCiclo = idCiclo;
        this.anio = anio;
    }

    public int getId() {
        return id;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public String getMaestro() {
        return maestro;
    }

    public int getIdCurriculoCurso() {
        return idCurriculoCurso;
    }

    public String getCurso() {
        return curso;
    }

    public int getIdSeccion() {
        return idSeccion;
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
}