package dto.biblioteca;

import exception.DatosInvalidosException;

public class PeticionDevolucion {

    private String observacion;

    public String getObservacion() {
        if (observacion == null || observacion.trim().isEmpty()) {
            return null;
        }
        return observacion.trim();
    }

    public void validar() throws DatosInvalidosException {
        if (getObservacion() != null && getObservacion().length() > 500) {
            throw new DatosInvalidosException("La observación no puede pasar de 500 caracteres");
        }
    }
}