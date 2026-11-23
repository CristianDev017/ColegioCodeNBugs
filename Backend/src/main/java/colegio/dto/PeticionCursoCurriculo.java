package colegio.dto;

import colegio.exception.DatosInvalidosException;

public class PeticionCursoCurriculo {

    private int idCurso;

    public int getIdCurso() {
        return idCurso;
    }

    public void validar() throws DatosInvalidosException {
        if (idCurso <= 0) {
            throw new DatosInvalidosException("Selecciona un curso");
        }
    }
}

