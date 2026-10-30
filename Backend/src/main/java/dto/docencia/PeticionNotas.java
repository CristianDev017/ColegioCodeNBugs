package dto.docencia;

import exception.DatosInvalidosException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PeticionNotas {

    public static class Registro {

        private int idInscripcion;
        private BigDecimal valor;

        public int getIdInscripcion() {
            return idInscripcion;
        }

        public BigDecimal getValor() {
            return valor;
        }
    }

    private int idPonderacion;
    private List<Registro> notas;

    public int getIdPonderacion() {
        return idPonderacion;
    }

    public List<Registro> getNotas() {
        return notas;
    }

    public void validar() throws DatosInvalidosException {
        if (idPonderacion < 1) {
            throw new DatosInvalidosException("Debes elegir una actividad");
        }
        if (notas == null || notas.isEmpty()) {
            throw new DatosInvalidosException("No hay notas para guardar");
        }
        if (notas.size() > 200) {
            throw new DatosInvalidosException("Son demasiadas notas en una sola petición");
        }
        Set<Integer> vistos = new HashSet<>();
        for (Registro r : notas) {
            if (r == null || r.getIdInscripcion() < 1 || !vistos.add(r.getIdInscripcion())) {
                throw new DatosInvalidosException("Hay estudiantes repetidos o inválidos");
            }
            if (r.getValor() == null) {
                throw new DatosInvalidosException("Todas las notas deben tener un valor");
            }
            if (r.getValor().signum() < 0) {
                throw new DatosInvalidosException("Una nota no puede ser negativa");
            }
            if (r.getValor().stripTrailingZeros().scale() > 2) {
                throw new DatosInvalidosException("Las notas solo pueden tener 2 decimales");
            }
        }
    }
}