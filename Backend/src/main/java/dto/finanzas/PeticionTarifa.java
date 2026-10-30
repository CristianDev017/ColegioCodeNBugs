package dto.finanzas;

import exception.DatosInvalidosException;

import java.math.BigDecimal;

public class PeticionTarifa {

    private int idGrado;
    private int idCiclo;
    private BigDecimal montoInscripcion;
    private BigDecimal montoMensualidad;
    private int diaVencimiento;

    public int getIdGrado() {
        return idGrado;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public BigDecimal getMontoInscripcion() {
        return montoInscripcion;
    }

    public BigDecimal getMontoMensualidad() {
        return montoMensualidad;
    }

    public int getDiaVencimiento() {
        return diaVencimiento;
    }

    public void validar() throws DatosInvalidosException {
        if (idCiclo < 1) {
            throw new DatosInvalidosException("Debes elegir un año lectivo");
        }
        if (idGrado < 1) {
            throw new DatosInvalidosException("Debes elegir un grado");
        }
        validarMontos();
    }

    public void validarMontos() throws DatosInvalidosException {
        revisarMonto(montoInscripcion, "de inscripción");
        revisarMonto(montoMensualidad, "de mensualidad");
        if (diaVencimiento < 1 || diaVencimiento > 31) {
            throw new DatosInvalidosException("El día de vencimiento debe estar entre 1 y 31");
        }
    }

    private void revisarMonto(BigDecimal monto, String nombre) throws DatosInvalidosException {
        if (monto == null) {
            throw new DatosInvalidosException("El monto " + nombre + " es obligatorio");
        }
        if (monto.signum() < 0) {
            throw new DatosInvalidosException("El monto " + nombre + " no puede ser negativo");
        }
        if (monto.stripTrailingZeros().scale() > 2) {
            throw new DatosInvalidosException("El monto " + nombre + " solo puede tener 2 decimales");
        }
        if (monto.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new DatosInvalidosException("El monto " + nombre + " es demasiado grande");
        }
    }
}