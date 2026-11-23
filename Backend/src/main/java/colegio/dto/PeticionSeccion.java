package colegio.dto;

import colegio.exception.DatosInvalidosException;

public class PeticionSeccion {

    private int idCicloGrado;
    private String nombre;
    private int cupo;

    public int getIdCicloGrado() {
        return idCicloGrado;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCupo() {
        return cupo;
    }

    public void validar() throws DatosInvalidosException {

        if (idCicloGrado <= 0) {
            throw new DatosInvalidosException(
                    "Selecciona un grado del ciclo escolar"
            );
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new DatosInvalidosException(
                    "El nombre de la seccion es obligatorio"
            );
        }

        nombre = nombre.trim().toUpperCase();

        if (nombre.length() > 10) {
            throw new DatosInvalidosException(
                    "El nombre no puede superar 10 caracteres"
            );
        }

        if (cupo <= 0) {
            throw new DatosInvalidosException(
                    "El cupo debe ser mayor que cero"
            );
        }
    }
}
