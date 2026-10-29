package dto;

import exception.DatosInvalidosException;

public class PeticionAsignacion {

    private int idEmpleado;
    private int idCurriculoCurso;
    private int idSeccion;

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public int getIdCurriculoCurso() {
        return idCurriculoCurso;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void validar() throws DatosInvalidosException {
        validarMaestro();
        if (idSeccion < 1) {
            throw new DatosInvalidosException("Debes elegir una sección");
        }
        if (idCurriculoCurso < 1) {
            throw new DatosInvalidosException("Debes elegir un curso");
        }
    }

    public void validarMaestro() throws DatosInvalidosException {
        if (idEmpleado < 1) {
            throw new DatosInvalidosException("Debes elegir un maestro");
        }
    }
}