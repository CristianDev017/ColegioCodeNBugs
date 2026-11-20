package colegio.model;

public class Nivel {

    private int idNivel;
    private String nombre;

    public Nivel(int idNivel, String nombre) {
        this.idNivel = idNivel;
        this.nombre = nombre;
    }

    public int getIdNivel() {
        return idNivel;
    }

    public String getNombre() {
        return nombre;
    }
}
