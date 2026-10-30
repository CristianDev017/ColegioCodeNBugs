package dto.docencia;

import exception.DatosInvalidosException;

import java.math.BigDecimal;

public class PeticionPonderacion {

    private String nombre;
    private BigDecimal porcentaje;
    private BigDecimal notaMaxima;

    public String getNombre() {
        return nombre == null ? null : nombre.trim();
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public void validar() throws DatosInvalidosException {
        if (getNombre() == null || getNombre().isEmpty()) {
            throw new DatosInvalidosException("El nombre de la actividad es obligatorio");
        }
        if (getNombre().length() > 60) {
            throw new DatosInvalidosException("El nombre no puede pasar de 60 caracteres");
        }
        if (porcentaje == null) {
            throw new DatosInvalidosException("El porcentaje es obligatorio");
        }
        if (porcentaje.signum() <= 0 || porcentaje.compareTo(new BigDecimal("100")) > 0) {
            throw new DatosInvalidosException("El porcentaje debe ser mayor que 0 y no pasar de 100");
        }
        if (porcentaje.stripTrailingZeros().scale() > 2) {
            throw new DatosInvalidosException("El porcentaje solo puede tener 2 decimales");
        }
        if (notaMaxima == null) {
            throw new DatosInvalidosException("La nota máxima es obligatoria");
        }
        if (notaMaxima.signum() <= 0 || notaMaxima.compareTo(new BigDecimal("999.99")) > 0) {
            throw new DatosInvalidosException("La nota máxima debe ser mayor que 0 y no pasar de 999.99");
        }
        if (notaMaxima.stripTrailingZeros().scale() > 2) {
            throw new DatosInvalidosException("La nota máxima solo puede tener 2 decimales");
        }
    }
}