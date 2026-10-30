package model.biblioteca;

public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private int cantidadTotal;
    private int cantidadDisponible;
    private boolean activo;

    public Libro(int id, String titulo, String autor, String editorial, int cantidadTotal,
                 int cantidadDisponible, boolean activo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.cantidadTotal = cantidadTotal;
        this.cantidadDisponible = cantidadDisponible;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getEditorial() {
        return editorial;
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public boolean isActivo() {
        return activo;
    }
}