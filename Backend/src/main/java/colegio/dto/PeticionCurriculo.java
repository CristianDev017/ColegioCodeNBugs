
package colegio.dto;

import colegio.exception.DatosInvalidosException;

public class PeticionCurriculo {

    private int idGrado;
    private int idCiclo;

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public void validar() throws DatosInvalidosException {
        if (idGrado <= 0) {
            throw new DatosInvalidosException("Selecciona un grado");
        }

        if (idCiclo <= 0) {
            throw new DatosInvalidosException("Selecciona un ciclo escolar");
        }
    }
}
