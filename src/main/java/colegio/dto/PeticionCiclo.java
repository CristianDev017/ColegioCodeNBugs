
package colegio.dto;

import colegio.exception.DatosInvalidosException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class PeticionCiclo {

    private int anio;
    private String fechaInicio;
    private String fechaFin;

    public int getAnio() {
        return anio;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void validar() throws DatosInvalidosException {

        if (anio < 2000 || anio > 2100) {
            throw new DatosInvalidosException("El año lectivo no es valido");
        }

        if (fechaInicio == null || fechaFin == null) {
            throw new DatosInvalidosException("Debes indicar ambas fechas");
        }

        try {
            LocalDate inicio = LocalDate.parse(fechaInicio);
            LocalDate fin = LocalDate.parse(fechaFin);

            if (!fin.isAfter(inicio)) {
                throw new DatosInvalidosException(
                        "La fecha final debe ser posterior a la inicial"
                );
            }

        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("El formato de las fechas no es valido");
        }
    }
}
