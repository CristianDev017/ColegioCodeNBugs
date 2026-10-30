package dto.biblioteca;

import exception.DatosInvalidosException;

import java.math.BigDecimal;

public class PeticionConfigBiblioteca {

    private Integer diasPrestamo;
    private BigDecimal multaPorDia;

    public int getDiasPrestamo() {
        return diasPrestamo;
    }

    public BigDecimal getMultaPorDia() {
        return multaPorDia;
    }

    public void validar() throws DatosInvalidosException {
        if (diasPrestamo == null) {
            throw new DatosInvalidosException("Los días de préstamo son obligatorios");
        }
        if (diasPrestamo < 1 || diasPrestamo > 365) {
            throw new DatosInvalidosException("Los días de préstamo deben estar entre 1 y 365");
        }
        if (multaPorDia == null) {
            throw new DatosInvalidosException("La multa por día es obligatoria");
        }
        if (multaPorDia.signum() < 0) {
            throw new DatosInvalidosException("La multa por día no puede ser negativa");
        }
        if (multaPorDia.scale() > 2) {
            throw new DatosInvalidosException("La multa por día solo puede tener 2 decimales");
        }
        if (multaPorDia.compareTo(new BigDecimal("999999.99")) > 0) {
            throw new DatosInvalidosException("La multa por día es demasiado grande");
        }
    }
}
