package dto;

import exception.DatosInvalidosException;

import java.math.BigDecimal;

public class PeticionActividad {

    private String nombre;
    private BigDecimal costo;
    private int cupoTotal;

    public String getNombre() {
        return nombre == null ? null : nombre.trim();
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public int getCupoTotal() {
        return cupoTotal;
    }

    public void validar() throws DatosInvalidosException {
        if (getNombre() == null || getNombre().isEmpty()) {
            throw new DatosInvalidosException("El nombre de la actividad es obligatorio");
        }
        if (getNombre().length() > 100) {
            throw new DatosInvalidosException("El nombre no puede pasar de 100 caracteres");
        }
        if (costo == null) {
            throw new DatosInvalidosException("El costo es obligatorio");
        }
        if (costo.signum() <= 0) {
            throw new DatosInvalidosException("El costo debe ser mayor a 0");
        }
        if (costo.stripTrailingZeros().scale() > 2) {
            throw new DatosInvalidosException("El costo solo puede tener 2 decimales");
        }
        if (costo.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new DatosInvalidosException("El costo es demasiado grande");
        }
        if (cupoTotal < 1) {
            throw new DatosInvalidosException("El cupo debe ser mayor a 0");
        }
    }
}