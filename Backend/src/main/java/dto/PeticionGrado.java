package dto;

import exception.DatosInvalidosException;

public class PeticionGrado {

    private Integer idNivel;
    private Integer idCarrera;
    private String nombre;
    private Integer cupoDefault;

    public Integer getIdNivel() {
        return idNivel;
    }

    public Integer getIdCarrera() {
        return idCarrera;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getCupoDefault() {
        return cupoDefault;
    }

    public void validar() throws DatosInvalidosException {
        if (idNivel == null || nombre == null || cupoDefault == null) {
            throw new DatosInvalidosException("Faltan datos del grado");
        }
        nombre = nombre.trim();
        if (nombre.isEmpty() || nombre.length() > 60) {
            throw new DatosInvalidosException("El nombre debe tener entre 1 y 60 caracteres");
        }
        if (cupoDefault < 1) {
            throw new DatosInvalidosException("El cupo debe ser mayor a cero");
        }
    }
}