package dto;

import exception.DatosInvalidosException;

public class PeticionSeccion {

    private Integer idGrado;
    private String nombre;
    private Integer cupo;

    public Integer getIdGrado() {
        return idGrado;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCupo() {
        return cupo;
    }

    public void validar() throws DatosInvalidosException {
        if (idGrado == null || nombre == null) {
            throw new DatosInvalidosException("Faltan datos de la sección");
        }
        nombre = nombre.trim();
        if (nombre.isEmpty() || nombre.length() > 10) {
            throw new DatosInvalidosException("El nombre debe tener entre 1 y 10 caracteres");
        }
        if (cupo != null && cupo < 1) {
            throw new DatosInvalidosException("El cupo debe ser mayor a cero");
        }
    }
}