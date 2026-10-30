package dto.biblioteca;

import exception.DatosInvalidosException;

public class PeticionPrestamo {

    private Integer idLibro;
    private String tipoSolicitante;
    private Integer idSolicitante;

    public int getIdLibro() {
        return idLibro;
    }

    public String getTipoSolicitante() {
        return tipoSolicitante;
    }

    public int getIdSolicitante() {
        return idSolicitante;
    }

    public void validar() throws DatosInvalidosException {
        if (idLibro == null) {
            throw new DatosInvalidosException("Debes elegir un libro");
        }
        if (tipoSolicitante == null
                || (!tipoSolicitante.equals("ESTUDIANTE") && !tipoSolicitante.equals("EMPLEADO"))) {
            throw new DatosInvalidosException("El tipo de solicitante no es válido");
        }
        if (idSolicitante == null) {
            throw new DatosInvalidosException("Debes elegir al solicitante");
        }
    }
}
