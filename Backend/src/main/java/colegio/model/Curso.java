package colegio.model;

public class Curso {

    private int idCurso;
    private String nombre;
    private String descripcion;
    private boolean activo;

    public Curso(int idCurso, String nombre, String descripcion, boolean activo) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActivo() {
        return activo;
    }
}
