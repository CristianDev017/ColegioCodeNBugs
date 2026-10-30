package service.docencia;

import dao.docencia.MaestroDao;
import dao.docencia.NotaDao;
import dao.docencia.PonderacionDao;
import dto.docencia.PeticionNotas;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.docencia.AlumnoNota;
import model.docencia.ContextoAsignacion;
import model.docencia.Ponderacion;

import java.sql.SQLException;
import java.util.List;

public class NotaService {

    private final NotaDao notaDao = new NotaDao();
    private final PonderacionDao ponderacionDao = new PonderacionDao();
    private final MaestroDao maestroDao = new MaestroDao();
    private final MaestroService maestroService = new MaestroService();

    public List<AlumnoNota> listar(int idUsuario, int idAsignacion, int idPonderacion, int pagina, int tamano)
            throws SQLException, ServicioException {
        ContextoAsignacion ctx = maestroService.contexto(idUsuario, idAsignacion);
        buscarActividad(idAsignacion, idPonderacion);
        return notaDao.listar(ctx, idPonderacion, pagina, tamano);
    }

    public void guardar(int idUsuario, int idAsignacion, PeticionNotas datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de las notas");
        }
        datos.validar();

        ContextoAsignacion ctx = maestroService.contexto(idUsuario, idAsignacion);
        if (!ctx.isCicloActivo()) {
            throw new ConflictoException("Solo se pueden ingresar notas en el año lectivo activo");
        }

        Ponderacion actividad = buscarActividad(idAsignacion, datos.getIdPonderacion());
        for (PeticionNotas.Registro r : datos.getNotas()) {
            if (r.getValor().compareTo(actividad.getNotaMaxima()) > 0) {
                throw new DatosInvalidosException("Una nota no puede pasar de la nota máxima de la actividad ("
                        + actividad.getNotaMaxima().stripTrailingZeros().toPlainString() + ")");
            }
            if (!maestroDao.inscripcionDeLaSeccion(r.getIdInscripcion(), ctx)) {
                throw new DatosInvalidosException("Hay estudiantes que no pertenecen a esta sección");
            }
        }
        notaDao.guardar(idUsuario, datos.getIdPonderacion(), datos.getNotas());
    }

    private Ponderacion buscarActividad(int idAsignacion, int idPonderacion) throws SQLException, NoEncontradoException {
        Ponderacion actividad = ponderacionDao.buscarPorId(idPonderacion, idAsignacion);
        if (actividad == null) {
            throw new NoEncontradoException("Debes elegir una actividad de este curso");
        }
        return actividad;
    }
}