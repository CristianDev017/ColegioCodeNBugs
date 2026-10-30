package service.finanzas;

import dao.finanzas.PagoDao;
import dao.finanzas.SolvenciaDao;
import dto.finanzas.PeticionSolvencia;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.finanzas.Solvencia;
import model.finanzas.Tarifa;
import model.finanzas.VerificacionSolvencia;
import model.finanzas.VerificacionSolvencia.Pendiente;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class SolvenciaService {

    private static final String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

    private final SolvenciaDao solvenciaDao = new SolvenciaDao();
    private final PagoDao pagoDao = new PagoDao();

    public List<Solvencia> listar(String buscar, int idCiclo, int pagina, int tamano) throws SQLException {
        return solvenciaDao.listar(buscar, idCiclo, pagina, tamano);
    }

    public int contar(String buscar, int idCiclo) throws SQLException {
        return solvenciaDao.contar(buscar, idCiclo);
    }

    public Solvencia obtener(int id) throws SQLException, NoEncontradoException {
        Solvencia solvencia = solvenciaDao.buscarPorId(id);
        if (solvencia == null) {
            throw new NoEncontradoException("La solvencia no existe");
        }
        return solvencia;
    }

    public VerificacionSolvencia verificar(int idEstudiante, int idCiclo) throws SQLException, ServicioException {
        if (pagoDao.datosEstudiante(idEstudiante) == null) {
            throw new NoEncontradoException("El estudiante no existe");
        }
        LocalDate[] fechas = solvenciaDao.fechasCiclo(idCiclo);
        if (fechas == null) {
            throw new NoEncontradoException("El año lectivo no existe");
        }
        Tarifa tarifa = pagoDao.tarifaDe(idEstudiante, idCiclo);
        if (tarifa == null) {
            throw new ConflictoException("El estudiante no está inscrito en ese año lectivo");
        }
        if (tarifa.getMontoInscripcion() == null) {
            throw new ConflictoException("El grado no tiene tarifa definida en ese año, no se puede calcular el saldo");
        }

        List<Pendiente> pendientes = new ArrayList<>();

        if (tarifa.getMontoInscripcion().signum() > 0
                && !pagoDao.existePago(idEstudiante, idCiclo, "INSCRIPCION", null, null)) {
            pendientes.add(new Pendiente("INSCRIPCION", null, "Inscripción", tarifa.getMontoInscripcion(), null));
        }

        if (tarifa.getMontoMensualidad().signum() > 0) {
            List<Integer> pagados = pagoDao.mesesPagados(idEstudiante, idCiclo);
            LocalDate hoy = LocalDate.now();
            YearMonth mes = YearMonth.from(fechas[0]);
            YearMonth ultimo = YearMonth.from(fechas[1]);
            while (!mes.isAfter(ultimo)) {
                int dia = Math.min(tarifa.getDiaVencimiento(), mes.lengthOfMonth());
                LocalDate vencimiento = mes.atDay(dia);
                boolean vencida = !vencimiento.isAfter(hoy);
                if (vencida && !pagados.contains(mes.getMonthValue())) {
                    pendientes.add(new Pendiente("MENSUALIDAD", mes.getMonthValue(),
                            "Mensualidad de " + MESES[mes.getMonthValue() - 1],
                            tarifa.getMontoMensualidad(), vencimiento.toString()));
                }
                mes = mes.plusMonths(1);
            }
        }
        return new VerificacionSolvencia(pendientes);
    }

    public Solvencia emitir(PeticionSolvencia datos, int idUsuario) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la solvencia");
        }
        datos.validar();

        VerificacionSolvencia verificacion = verificar(datos.getIdEstudiante(), datos.getIdCiclo());
        if (!verificacion.isSolvente()) {
            StringBuilder detalle = new StringBuilder();
            for (Pendiente p : verificacion.getPendientes()) {
                if (detalle.length() > 0) {
                    detalle.append(", ");
                }
                detalle.append(p.getDetalle()).append(" (Q").append(p.getMonto().setScale(2)).append(")");
            }
            throw new ConflictoException("El estudiante tiene saldos pendientes: " + detalle);
        }

        int id = solvenciaDao.crear(datos.getIdEstudiante(), datos.getIdCiclo(), idUsuario);
        return obtener(id);
    }
}