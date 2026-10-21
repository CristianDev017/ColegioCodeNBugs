package dto;

import exception.DatosInvalidosException;

public class PeticionCarrera {

    private String nombre;
    private String descripcion;

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void validar() throws DatosInvalidosException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre de la carrera es obligatorio");
        }
        nombre = nombre.trim();
        if (nombre.length() > 100) {
            throw new DatosInvalidosException("El nombre no puede pasar de 100 caracteres");
        }
        if (descripcion != null) {
            descripcion = descripcion.trim();
            if (descripcion.isEmpty()) {
                descripcion = null;
            } else if (descripcion.length() > 255) {
                throw new DatosInvalidosException("La descripción no puede pasar de 255 caracteres");
            }
        }
    }
}