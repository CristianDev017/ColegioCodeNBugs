
package service.reportes;

import dao.reportes.HistorialPagoDao;
import exception.DatosInvalidosException;
import model.reportes.HistorialPago;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialPagoService {

    private final HistorialPagoDao dao = new HistorialPagoDao();

    public List<HistorialPago> listar(
            int idEstudiante,
            LocalDate desde,
            LocalDate hasta) throws SQLException, DatosInvalidosException {

        validar(idEstudiante, desde, hasta);

        return dao.listar(idEstudiante, desde, hasta);
    }

    public BigDecimal totalPagado(
            int idEstudiante,
            LocalDate desde,
            LocalDate hasta) throws SQLException, DatosInvalidosException {

        validar(idEstudiante, desde, hasta);

        return dao.totalPagado(idEstudiante, desde, hasta);
    }

    public List<HistorialPago> pagina(
            List<HistorialPago> lista,
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
            int idEstudiante,
            LocalDate desde,
            LocalDate hasta) throws DatosInvalidosException {

        if (idEstudiante <= 0) {
            throw new DatosInvalidosException(
                    "Debes seleccionar un estudiante"
            );
        }

        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException(
                    "La fecha inicial no puede ser mayor que la fecha final"
            );
        }
    }
}
