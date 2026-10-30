package model.personal;

public class OpcionEmpleado {

    private int id;
    private String nombre;

    public OpcionEmpleado(int id, String nombre) {
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