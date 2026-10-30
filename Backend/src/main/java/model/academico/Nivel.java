package model.academico;

public class Nivel {

    private int id;
    private String nombre;

    public Nivel(int id, String nombre) {
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