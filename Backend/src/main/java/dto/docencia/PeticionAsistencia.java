package dto.docencia;

import exception.DatosInvalidosException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeticionAsistencia {

    public static class Registro {

        private int idInscripcion;
        private String estado;

        public int getIdInscripcion() {
            return idInscripcion;
        }

        public String getEstado() {
            return estado;
        }
    }

    private static final List<String> ESTADOS = List.of("PRESENTE", "AUSENTE", "TARDANZA");

    private String fecha;
    private List<Registro> registros;

    public String getFecha() {
        return fecha;
    }

    public List<Registro> getRegistros() {
        return registros;
    }

    public void validar() throws DatosInvalidosException {
        if (fecha == null || fecha.isEmpty()) {
            throw new DatosInvalidosException("La fecha es obligatoria");
        }
        LocalDate dia;
        try {
            dia = LocalDate.parse(fecha);
        } catch (DateTimeParseException e) {
            throw new DatosInvalidosException("La fecha no es válida");
        }
        if (dia.isAfter(LocalDate.now())) {
            throw new DatosInvalidosException("No se puede tomar asistencia de una fecha futura");
        }
        if (registros == null || registros.isEmpty()) {
            throw new DatosInvalidosException("No hay estudiantes para guardar");
        }
        if (registros.size() > 200) {
            throw new DatosInvalidosException("Son demasiados registros en una sola petición");
        }
        Set<Integer> vistos = new HashSet<>();
        for (Registro r : registros) {
            if (r == null || r.getEstado() == null || !ESTADOS.contains(r.getEstado())) {
                throw new DatosInvalidosException("El estado de asistencia no es válido");
            }
            if (r.getIdInscripcion() < 1 || !vistos.add(r.getIdInscripcion())) {
                throw new DatosInvalidosException("Hay estudiantes repetidos o inválidos");
            }
        }
    }
}