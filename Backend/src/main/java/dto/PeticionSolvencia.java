package dto;

import exception.DatosInvalidosException;

public class PeticionSolvencia {

    private int idEstudiante;
    private int idCiclo;

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void validar() throws DatosInvalidosException {
        if (idEstudiante < 1) {
            throw new DatosInvalidosException("Debes elegir un estudiante");
        }
        if (idCiclo < 1) {
            throw new DatosInvalidosException("Debes elegir un año lectivo");
        }
    }
}