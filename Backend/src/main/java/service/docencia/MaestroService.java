package service.docencia;

import dao.docencia.MaestroDao;
import dto.docencia.PeticionAsistencia;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.docencia.AlumnoNomina;
import model.docencia.ContextoAsignacion;
import model.docencia.CursoMaestro;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MaestroService {

    private final MaestroDao maestroDao = new MaestroDao();

    public List<CursoMaestro> listarCursos(int idUsuario, int pagina, int tamano) throws SQLException {
        return maestroDao.listarCursos(idUsuario, pagina, tamano);
    }

    public int contarCursos(int idUsuario) throws SQLException {
        return maestroDao.contarCursos(idUsuario);
    }

    public CursoMaestro obtenerCurso(int idUsuario, int idAsignacion) throws SQLException, NoEncontradoException {
        CursoMaestro curso = maestroDao.buscarCurso(idUsuario, idAsignacion);
        if (curso == null) {
            throw new NoEncontradoException("El curso no existe o no está asignado a ti");
        }
        return curso;
    }

    public ContextoAsignacion contexto(int idUsuario, int idAsignacion) throws SQLException, NoEncontradoException {
        ContextoAsignacion ctx = maestroDao.contexto(idUsuario, idAsignacion);
        if (ctx == null) {
            throw new NoEncontradoException("El curso no existe o no está asignado a ti");
        }
        return ctx;
    }

    public List<AlumnoNomina> nomina(int idUsuario, int idAsignacion, String fecha, int pagina, int tamano)
            throws SQLException, ServicioException {
        ContextoAsignacion ctx = contexto(idUsuario, idAsignacion);
        if (fecha != null && !fecha.isEmpty()) {
            try {
                LocalDate.parse(fecha);
            } catch (DateTimeParseException e) {
                throw new DatosInvalidosException("La fecha no es válida");
            }
        } else {
            fecha = null;
        }
        return maestroDao.nomina(idAsignacion, ctx, fecha, pagina, tamano);
    }

    public int contarNomina(int idUsuario, int idAsignacion) throws SQLException, ServicioException {
        return maestroDao.contarNomina(contexto(idUsuario, idAsignacion));
    }

    public void registrarAsistencia(int idUsuario, int idAsignacion, PeticionAsistencia datos)
            throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la asistencia");
        }
        datos.validar();

        ContextoAsignacion ctx = contexto(idUsuario, idAsignacion);
        if (!ctx.isCicloActivo()) {
            throw new ConflictoException("Solo se puede tomar asistencia en el año lectivo activo");
        }

        LocalDate dia = LocalDate.parse(datos.getFecha());
        if (dia.isBefore(LocalDate.parse(ctx.getFechaInicio())) || dia.isAfter(LocalDate.parse(ctx.getFechaFin()))) {
            throw new ConflictoException("La fecha está fuera del rango del año lectivo");
        }

        for (PeticionAsistencia.Registro r : datos.getRegistros()) {
            if (!maestroDao.inscripcionDeLaSeccion(r.getIdInscripcion(), ctx)) {
                throw new DatosInvalidosException("Hay estudiantes que no pertenecen a esta sección");
            }
        }
        maestroDao.guardarAsistencia(idAsignacion, datos.getFecha(), datos.getRegistros());
    }
}