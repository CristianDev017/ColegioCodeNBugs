package dto;

import exception.DatosInvalidosException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PeticionPago {

    private int idEstudiante;
    private int idCiclo;
    private String concepto;
    private Integer mes;
    private Integer idActividad;
    private BigDecimal monto;
    private String fechaPago;

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public int getIdCiclo() {
        return idCiclo;
    }

    public String getConcepto() {
        return concepto == null ? "" : concepto.trim().toUpperCase();
    }

    public Integer getMes() {
        return mes;
    }

    public Integer getIdActividad() {
        return idActividad;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDate getFecha() {
        return LocalDate.parse(fechaPago.trim());
    }

    public void validar() throws DatosInvalidosException {
        if (idEstudiante < 1) {
            throw new DatosInvalidosException("Debes elegir un estudiante");
        }
        if (idCiclo < 1) {
            throw new DatosInvalidosException("Debes elegir un año lectivo");
        }

        String c = getConcepto();
        if (!c.equals("INSCRIPCION") && !c.equals("MENSUALIDAD") && !c.equals("ACTIVIDAD")) {
            throw new DatosInvalidosException("El concepto del pago no es válido");
        }
        if (c.equals("MENSUALIDAD")) {
            if (mes == null || mes < 1 || mes > 12) {
                throw new DatosInvalidosException("El mes debe estar entre 1 y 12");
            }
        } else if (mes != null) {
            throw new DatosInvalidosException("Solo la mensualidad lleva mes");
        }
        if (c.equals("ACTIVIDAD")) {
            if (idActividad == null || idActividad < 1) {
                throw new DatosInvalidosException("Debes elegir una actividad");
            }
        } else if (idActividad != null) {
            throw new DatosInvalidosException("Solo el pago de actividad lleva actividad");
        }

        if (monto == null) {
            throw new DatosInvalidosException("El monto es obligatorio");
        }
        if (monto.signum() <= 0) {
            throw new DatosInvalidosException("El monto debe ser mayor a 0");
        }
        if (monto.stripTrailingZeros().scale() > 2) {
            throw new DatosInvalidosException("El monto solo puede tener 2 decimales");
        }
        if (monto.compareTo(new BigDecimal("99999999.99")) > 0) {
            throw new DatosInvalidosException("El monto es demasiado grande");
        }

        if (fechaPago == null || fechaPago.isBlank()) {
            throw new DatosInvalidosException("La fecha de pago es obligatoria");
        }
        try {
            if (getFecha().isAfter(LocalDate.now())) {
                throw new DatosInvalidosException("La fecha de pago no puede ser futura");
            }
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("La fecha de pago no es válida");
        }
    }
}