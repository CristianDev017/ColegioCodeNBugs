
package service.reportes;

import dao.reportes.RendimientoAcademicoDao;
import exception.DatosInvalidosException;
import model.reportes.RendimientoAcademico;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RendimientoAcademicoService {

    private final RendimientoAcademicoDao dao = new RendimientoAcademicoDao();

    public List<RendimientoAcademico> listar(
            int idCiclo,
            int idGrado,
            int idSeccion) throws SQLException, DatosInvalidosException {

        if (idCiclo <= 0) {
            throw new DatosInvalidosException("Debes seleccionar un año lectivo");
        }

        if (idGrado < 0) {
            throw new DatosInvalidosException("El grado seleccionado no es válido");
        }

        if (idSeccion < 0) {
            throw new DatosInvalidosException("La sección seleccionada no es válida");
        }

        List<RendimientoAcademico> lista = dao.listar(
                idCiclo,
                idGrado,
                idSeccion
        );

        clasificar(lista);

        return lista;
    }

    private void clasificar(List<RendimientoAcademico> lista) {

        Map<String, List<RendimientoAcademico>> grupos = new HashMap<>();

        for (RendimientoAcademico fila : lista) {

            String clave = fila.getGrado() + "|" + fila.getSeccion();

            grupos.computeIfAbsent(
                    clave,
                    k -> new ArrayList<>()
            ).add(fila);
        }

        for (List<RendimientoAcademico> grupo : grupos.values()) {

            BigDecimal mayor = null;
            BigDecimal menor = null;
            int evaluados = 0;

            for (RendimientoAcademico fila : grupo) {

                BigDecimal promedio = fila.getPromedio();

                if (promedio == null) {
                    continue;
                }

                evaluados++;

                if (mayor == null || promedio.compareTo(mayor) > 0) {
                    mayor = promedio;
                }

                if (menor == null || promedio.compareTo(menor) < 0) {
                    menor = promedio;
                }
            }

            for (RendimientoAcademico fila : grupo) {

                BigDecimal promedio = fila.getPromedio();

                if (promedio == null) {
                    fila.setRendimiento("Sin notas");
                } else if (evaluados == 1) {
                    fila.setRendimiento("Único curso evaluado");
                } else if (mayor != null && menor != null
                        && mayor.compareTo(menor) == 0) {
                    fila.setRendimiento("Promedios iguales");
                } else if (promedio.compareTo(mayor) == 0) {
                    fila.setRendimiento("Mejor promedio");
                } else if (promedio.compareTo(menor) == 0) {
                    fila.setRendimiento("Mayor dificultad");
                } else {
                    fila.setRendimiento("Intermedio");
                }
            }
        }
    }

    public List<RendimientoAcademico> pagina(
            List<RendimientoAcademico> lista,
            int pagina,
            int tamano) {

        int inicio = (pagina - 1) * tamano;

        if (inicio >= lista.size()) {
            return new ArrayList<>();
        }

        int fin = Math.min(lista.size(), inicio + tamano);

        return new ArrayList<>(lista.subList(inicio, fin));
    }
}
