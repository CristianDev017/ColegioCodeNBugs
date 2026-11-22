package colegio.model;

public class Carrera {

    private int idCarrera;
    private String nombre;
    private String descripcion;
    private boolean activo;


    public Carrera(int idCarrera, String nombre, String descripcion, boolean activo) {
        this.idCarrera = idCarrera;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
    }

    public int getIdCarrera() {
        return idCarrera;
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
