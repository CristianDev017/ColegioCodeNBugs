
package service.reportes;

import dao.reportes.IngresoConceptoDao;
import exception.DatosInvalidosException;
import model.reportes.IngresoConcepto;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IngresoConceptoService {

    private final IngresoConceptoDao dao = new IngresoConceptoDao();

    public List<IngresoConcepto> listar(
            LocalDate desde,
            LocalDate hasta) throws SQLException, DatosInvalidosException {

        validar(desde, hasta);

        return dao.listar(desde, hasta);
    }

    public BigDecimal totalIngresos(
            LocalDate desde,
            LocalDate hasta) throws SQLException, DatosInvalidosException {

        validar(desde, hasta);

        return dao.totalIngresos(desde, hasta);
    }

    public List<IngresoConcepto> pagina(
            List<IngresoConcepto> lista,
            int pagina,
            int tamano) {

        int inicio = (pagina - 1) * tamano;

        if (inicio >= lista.size()) {
            return new ArrayList<>();
        }

        int fin = Math.min(lista.size(), inicio + tamano);

        return new ArrayList<>(lista.subList(inicio, fin));
    }

    private void validar(
            LocalDate desde,
            LocalDate hasta) throws DatosInvalidosException {

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException(
                    "La fecha inicial no puede ser mayor que la fecha final"
            );
        }
    }
}
