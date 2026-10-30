package dto.academico;

import exception.DatosInvalidosException;

public class PeticionCurriculoCurso {

    private int idCiclo;
    private int idGrado;
    private int idCurso;

    public int getIdCiclo() {
        return idCiclo;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void validar() throws DatosInvalidosException {
        if (idCiclo < 1) {
            throw new DatosInvalidosException("Debes elegir un año lectivo");
        }
        if (idGrado < 1) {
            throw new DatosInvalidosException("Debes elegir un grado");
        }
        if (idCurso < 1) {
            throw new DatosInvalidosException("Debes elegir un curso");
        }
    }
}