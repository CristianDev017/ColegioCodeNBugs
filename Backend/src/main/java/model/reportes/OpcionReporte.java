package model.reportes;

public class OpcionReporte {

    private int id;
    private String texto;

    public OpcionReporte(int id, String texto) {
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