package colegio.dto;

import colegio.exception.DatosInvalidosException;

public class PeticionGrado {

    private int idNivel;
    private Integer idCarrera;
    private String nombre;
    private int cupoDefault;

    public int getIdNivel() {
        return idNivel;
    }

    public Integer getIdCarrera() {
        return idCarrera;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupoDefault() {
        return cupoDefault;
    }

    public void validar() throws DatosInvalidosException {

        if (idNivel <= 0) {
            throw new DatosInvalidosException("Selecciona un nivel");
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatosInvalidosException("El nombre del grado es obligatorio");
        }

        nombre = nombre.trim();

        if (nombre.length() > 60) {
            throw new DatosInvalidosException("El nombre no puede superar 60 caracteres");
        }

        if (cupoDefault <= 0) {
            throw new DatosInvalidosException("El cupo debe ser mayor que cero");
        }

        if (idCarrera != null && idCarrera <= 0) {
            throw new DatosInvalidosException("La carrera seleccionada no es valida");
        }
    }
}
