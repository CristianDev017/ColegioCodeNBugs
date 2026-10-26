package dto;

import exception.DatosInvalidosException;

public class PeticionInscripcion {

    private int idEstudiante;
    private int idSeccion;

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void validar() throws DatosInvalidosException {
        if (idEstudiante < 1) {
            throw new DatosInvalidosException("Debes elegir un estudiante");
        }
        if (idSeccion < 1) {
            throw new DatosInvalidosException("Debes elegir un grado y sección");
        }
    }
}