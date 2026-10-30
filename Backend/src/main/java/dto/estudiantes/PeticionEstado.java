package dto.estudiantes;

import exception.DatosInvalidosException;

public class PeticionEstado {

    private String estado;

    public String getEstado() {
        return estado;
    }

    public void validar() throws DatosInvalidosException {
        if (estado == null) {
            throw new DatosInvalidosException("Falta el estado");
        }
        estado = estado.trim().toUpperCase();
        if (!(estado.equals("ACTIVO") || estado.equals("INACTIVO") || estado.equals("GRADUADO"))) {
            throw new DatosInvalidosException("El estado debe ser ACTIVO, INACTIVO o GRADUADO");
        }
    }
}