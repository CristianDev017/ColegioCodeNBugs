package model.biblioteca;

public class OpcionBiblioteca {

    private int id;
    private String texto;

    public OpcionBiblioteca(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }
}
