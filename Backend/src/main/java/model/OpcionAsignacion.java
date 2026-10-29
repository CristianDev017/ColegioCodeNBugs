package model;

public class OpcionAsignacion {

    private int id;
    private String nombre;

    public OpcionAsignacion(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}