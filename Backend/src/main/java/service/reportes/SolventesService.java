package service.reportes;

import dao.reportes.SolventesDao;
import exception.DatosInvalidosException;
import model.reportes.CuentaEstudiante;
import model.reportes.FilaSolvencia;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SolventesService {

    private final SolventesDao solventesDao = new SolventesDao();

    public List<FilaSolvencia> filas(int idCiclo, int idGrado, int idSeccion, String estado,
                                     LocalDate desde, LocalDate hasta) throws SQLException, DatosInvalidosException {
        if (idCiclo <= 0) {
            throw new DatosInvalidosException("Debes elegir un año lectivo");
        }
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new DatosInvalidosException("La fecha inicial no puede ser mayor a la final");
        }
        LocalDate hoy = LocalDate.now();
        List<FilaSolvencia> filas = new ArrayList<>();
        for (CuentaEstudiante cuenta : solventesDao.cuentas(idCiclo, idGrado, idSeccion)) {
            FilaSolvencia fila = calcular(cuenta, hoy, desde, hasta);
            if (estado.isEmpty() || fila.getEstado().equalsIgnoreCase(estado)) {
                filas.add(fila);
            }
        }
        return filas;
    }

    public List<FilaSolvencia> pagina(List<FilaSolvencia> filas, int pagina, int tamano) {
        int inicio = (pagina - 1) * tamano;
        if (inicio >= filas.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(filas.subList(inicio, Math.min(filas.size(), inicio + tamano)));
    }

    private FilaSolvencia calcular(CuentaEstudiante c, LocalDate hoy, LocalDate desde, LocalDate hasta) {
        BigDecimal deuda = BigDecimal.ZERO;
        boolean inscripcionPendiente = false;
        int mensualidadesPendientes = 0;

        if (!c.isInscripcionPagada() && c.getMontoInscripcion().signum() > 0
                && enRango(c.getFechaInicio(), desde, hasta)) {
            deuda = deuda.add(c.getMontoInscripcion());
            inscripcionPendiente = true;
        }

        Set<Integer> pagados = new HashSet<>();
        if (c.getMesesPagados() != null) {
            for (String mes : c.getMesesPagados().split(",")) {
                pagados.add(Integer.parseInt(mes.trim()));
            }
        }

        YearMonth mes = YearMonth.from(c.getFechaInicio());
        YearMonth ultimo = YearMonth.from(c.getFechaFin());
        while (!mes.isAfter(ultimo)) {
            LocalDate vencimiento = mes.atDay(Math.min(c.getDiaVencimiento(), mes.lengthOfMonth()));
            if (!vencimiento.isAfter(hoy) && enRango(vencimiento, desde, hasta)
                    && !pagados.contains(mes.getMonthValue())) {
                deuda = deuda.add(c.getMontoMensualidad());
                mensualidadesPendientes++;
            }
            mes = mes.plusMonths(1);
        }

        List<String> partes = new ArrayList<>();
        if (inscripcionPendiente) {
            partes.add("Inscripción");
        }
        if (mensualidadesPendientes > 0) {
            partes.add(mensualidadesPendientes + (mensualidadesPendientes == 1 ? " mensualidad" : " mensualidades"));
        }
        String detalle = partes.isEmpty() ? "-" : String.join(", ", partes);
        String estado = deuda.signum() > 0 ? "Moroso" : "Solvente";
        return new FilaSolvencia(c.getCarnet(), c.getEstudiante(), c.getGrado(), c.getSeccion(), estado, detalle, deuda);
    }

    private boolean enRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return (desde == null || !fecha.isBefore(desde)) && (hasta == null || !fecha.isAfter(hasta));
    }
}