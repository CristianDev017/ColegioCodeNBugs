package dto.biblioteca;

import exception.DatosInvalidosException;

public class PeticionLibro {

    private String titulo;
    private String autor;
    private String editorial;
    private Integer cantidadTotal;

    public String getTitulo() {
        return titulo == null ? null : titulo.trim();
    }

    public String getAutor() {
        return autor == null ? null : autor.trim();
    }

    public String getEditorial() {
        return editorial == null ? null : editorial.trim();
    }

    public int getCantidadTotal() {
        return cantidadTotal;
    }

    public void validar() throws DatosInvalidosException {
        if (getTitulo() == null || getTitulo().isEmpty()) {
            throw new DatosInvalidosException("El título es obligatorio");
        }
        if (getTitulo().length() > 150) {
            throw new DatosInvalidosException("El título no puede pasar de 150 caracteres");
        }
        if (getAutor() == null || getAutor().isEmpty()) {
            throw new DatosInvalidosException("El autor es obligatorio");
        }
        if (getAutor().length() > 100) {
            throw new DatosInvalidosException("El autor no puede pasar de 100 caracteres");
        }
        if (getEditorial() == null || getEditorial().isEmpty()) {
            throw new DatosInvalidosException("La editorial es obligatoria");
        }
        if (getEditorial().length() > 100) {
            throw new DatosInvalidosException("La editorial no puede pasar de 100 caracteres");
        }
        if (cantidadTotal == null) {
            throw new DatosInvalidosException("La cantidad es obligatoria");
        }
        if (cantidadTotal < 0) {
            throw new DatosInvalidosException("La cantidad no puede ser negativa");
        }
        if (cantidadTotal > 9999) {
            throw new DatosInvalidosException("La cantidad no puede pasar de 9999");
        }
    }
}