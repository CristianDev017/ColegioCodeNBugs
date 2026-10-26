package service;

import dao.ActividadDao;
import dao.PagoDao;
import dto.PeticionPago;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Actividad;
import model.EstadoCuenta;
import model.Pago;
import model.Tarifa;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PagoService {

    private final PagoDao pagoDao = new PagoDao();
    private final ActividadDao actividadDao = new ActividadDao();

    public List<Pago> listar(String buscar, int idCiclo, int pagina, int tamano) throws SQLException {
        return pagoDao.listar(buscar, idCiclo, pagina, tamano);
    }

    public int contar(String buscar, int idCiclo) throws SQLException {
        return pagoDao.contar(buscar, idCiclo);
    }

    public Pago obtener(int id) throws SQLException, NoEncontradoException {
        Pago pago = pagoDao.buscarPorId(id);
        if (pago == null) {
            throw new NoEncontradoException("El pago no existe");
        }
        return pago;
    }

    public EstadoCuenta estadoCuenta(int idEstudiante, int idCiclo) throws SQLException, ServicioException {
        String[] estudiante = pagoDao.datosEstudiante(idEstudiante);
        if (estudiante == null) {
            throw new NoEncontradoException("El estudiante no existe");
        }
        Tarifa tarifa = pagoDao.tarifaDe(idEstudiante, idCiclo);
        if (tarifa == null) {
            throw new ConflictoException("El estudiante no está inscrito en ese año lectivo");
        }
        boolean inscripcionPagada = pagoDao.existePago(idEstudiante, idCiclo, "INSCRIPCION", null, null);
        return new EstadoCuenta(idEstudiante, estudiante[0], estudiante[1], idCiclo, tarifa.getAnio(),
                tarifa.getGrado(), tarifa.getMontoInscripcion(), tarifa.getMontoMensualidad(),
                tarifa.getDiaVencimiento(), inscripcionPagada, pagoDao.mesesPagados(idEstudiante, idCiclo));
    }

    public Pago registrar(PeticionPago datos, int idUsuario) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del pago");
        }
        datos.validar();

        String[] estudiante = pagoDao.datosEstudiante(datos.getIdEstudiante());
        if (estudiante == null) {
            throw new NoEncontradoException("El estudiante no existe");
        }
        if (!estudiante[2].equals("ACTIVO")) {
            throw new ConflictoException("El estudiante no está activo");
        }

        String estadoCiclo = pagoDao.estadoCiclo(datos.getIdCiclo());
        if (estadoCiclo == null) {
            throw new NoEncontradoException("El año lectivo no existe");
        }
        if (estadoCiclo.equals("CERRADO")) {
            throw new ConflictoException("El año lectivo está cerrado, no se pueden registrar pagos");
        }

        Tarifa tarifa = pagoDao.tarifaDe(datos.getIdEstudiante(), datos.getIdCiclo());
        if (tarifa == null) {
            throw new ConflictoException("El estudiante no está inscrito en ese año lectivo");
        }

        String concepto = datos.getConcepto();
        BigDecimal esperado;
        if (concepto.equals("INSCRIPCION")) {
            esperado = montoDeTarifa(tarifa.getMontoInscripcion());
            if (pagoDao.existePago(datos.getIdEstudiante(), datos.getIdCiclo(), concepto, null, null)) {
                throw new ConflictoException("La inscripción de este año ya fue pagada");
            }
        } else if (concepto.equals("MENSUALIDAD")) {
            esperado = montoDeTarifa(tarifa.getMontoMensualidad());
            if (pagoDao.existePago(datos.getIdEstudiante(), datos.getIdCiclo(), concepto, datos.getMes(), null)) {
                throw new ConflictoException("La mensualidad de ese mes ya fue pagada");
            }
        } else {
            Actividad actividad = actividadDao.buscarPorId(datos.getIdActividad());
            if (actividad == null) {
                throw new NoEncontradoException("La actividad no existe");
            }
            if (!actividad.isActivo()) {
                throw new ConflictoException("La actividad está inactiva");
            }
            esperado = actividad.getCosto();
            if (pagoDao.existePago(datos.getIdEstudiante(), datos.getIdCiclo(), concepto, null, datos.getIdActividad())) {
                throw new ConflictoException("El estudiante ya pagó esa actividad");
            }
        }

        if (datos.getMonto().compareTo(esperado) != 0) {
            throw new DatosInvalidosException("El monto no coincide, se esperaba Q" + esperado.setScale(2));
        }

        int id = pagoDao.registrar(datos, idUsuario);
        if (id == -1) {
            throw new ConflictoException("La actividad no tiene cupos disponibles");
        }
        return obtener(id);
    }

    private BigDecimal montoDeTarifa(BigDecimal monto) throws ConflictoException {
        if (monto == null) {
            throw new ConflictoException("No hay tarifa definida para el grado del estudiante en ese año");
        }
        if (monto.signum() == 0) {
            throw new ConflictoException("La tarifa de este concepto es Q0.00, no hay nada que pagar");
        }
        return monto;
    }
}