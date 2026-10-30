package service.docencia;

import dao.docencia.PonderacionDao;
import dto.docencia.PeticionPonderacion;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.docencia.ContextoAsignacion;
import model.docencia.Ponderacion;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class PonderacionService {

    private final PonderacionDao ponderacionDao = new PonderacionDao();
    private final MaestroService maestroService = new MaestroService();

    public List<Ponderacion> listar(int idUsuario, int idAsignacion, int pagina, int tamano)
            throws SQLException, ServicioException {
        maestroService.contexto(idUsuario, idAsignacion);
        return ponderacionDao.listar(idAsignacion, pagina, tamano);
    }

    public int contar(int idUsuario, int idAsignacion) throws SQLException, ServicioException {
        maestroService.contexto(idUsuario, idAsignacion);
        return ponderacionDao.contar(idAsignacion);
    }

    public BigDecimal suma(int idUsuario, int idAsignacion) throws SQLException, ServicioException {
        maestroService.contexto(idUsuario, idAsignacion);
        return ponderacionDao.suma(idAsignacion, 0);
    }

    public Ponderacion obtener(int idUsuario, int idAsignacion, int idPonderacion)
            throws SQLException, ServicioException {
        maestroService.contexto(idUsuario, idAsignacion);
        return buscar(idAsignacion, idPonderacion);
    }

    public Ponderacion crear(int idUsuario, int idAsignacion, PeticionPonderacion datos)
            throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la ponderación");
        }
        datos.validar();
        revisarAnioActivo(idUsuario, idAsignacion);
        if (ponderacionDao.existeNombre(idAsignacion, datos.getNombre(), 0)) {
            throw new ConflictoException("Ya existe una actividad con ese nombre en este curso");
        }
        revisarSuma(idAsignacion, 0, datos.getPorcentaje());
        int id = ponderacionDao.crear(idAsignacion, datos);
        return buscar(idAsignacion, id);
    }

    public Ponderacion editar(int idUsuario, int idAsignacion, int idPonderacion, PeticionPonderacion datos)
            throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la ponderación");
        }
        datos.validar();
        revisarAnioActivo(idUsuario, idAsignacion);
        buscar(idAsignacion, idPonderacion);
        if (ponderacionDao.tieneNotas(idPonderacion)) {
            throw new ConflictoException("No se puede modificar: ya tiene notas registradas");
        }
        if (ponderacionDao.existeNombre(idAsignacion, datos.getNombre(), idPonderacion)) {
            throw new ConflictoException("Ya existe una actividad con ese nombre en este curso");
        }
        revisarSuma(idAsignacion, idPonderacion, datos.getPorcentaje());
        ponderacionDao.actualizar(idPonderacion, datos);
        return buscar(idAsignacion, idPonderacion);
    }

    public void eliminar(int idUsuario, int idAsignacion, int idPonderacion) throws SQLException, ServicioException {
        revisarAnioActivo(idUsuario, idAsignacion);
        buscar(idAsignacion, idPonderacion);
        if (ponderacionDao.tieneNotas(idPonderacion)) {
            throw new ConflictoException("No se puede eliminar: ya tiene notas registradas");
        }
        ponderacionDao.eliminar(idPonderacion);
    }

    private Ponderacion buscar(int idAsignacion, int idPonderacion) throws SQLException, NoEncontradoException {
        Ponderacion ponderacion = ponderacionDao.buscarPorId(idPonderacion, idAsignacion);
        if (ponderacion == null) {
            throw new NoEncontradoException("La ponderación no existe");
        }
        return ponderacion;
    }

    private void revisarAnioActivo(int idUsuario, int idAsignacion) throws SQLException, ServicioException {
        ContextoAsignacion ctx = maestroService.contexto(idUsuario, idAsignacion);
        if (!ctx.isCicloActivo()) {
            throw new ConflictoException("Solo se pueden modificar ponderaciones en el año lectivo activo");
        }
    }

    private void revisarSuma(int idAsignacion, int excluirId, BigDecimal nuevo) throws SQLException, ServicioException {
        BigDecimal otras = ponderacionDao.suma(idAsignacion, excluirId);
        if (otras.add(nuevo).compareTo(new BigDecimal("100")) > 0) {
            throw new ConflictoException("La suma de los porcentajes no puede pasar de 100 (las demás suman "
                    + otras.stripTrailingZeros().toPlainString() + ")");
        }
    }
}