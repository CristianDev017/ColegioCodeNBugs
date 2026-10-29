package service;

import dao.AsignacionDao;
import dto.PeticionAsignacion;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Asignacion;
import model.CursoSeccion;
import model.OpcionAsignacion;

import java.sql.SQLException;
import java.util.List;

public class AsignacionService {

    private final AsignacionDao asignacionDao = new AsignacionDao();

    public List<Asignacion> listar(int idEmpleado, int idSeccion, int pagina, int tamano) throws SQLException {
        return asignacionDao.listar(idEmpleado, idSeccion, pagina, tamano);
    }

    public int contar(int idEmpleado, int idSeccion) throws SQLException {
        return asignacionDao.contar(idEmpleado, idSeccion);
    }

    public Asignacion obtener(int id) throws SQLException, NoEncontradoException {
        Asignacion asignacion = asignacionDao.buscarPorId(id);
        if (asignacion == null) {
            throw new NoEncontradoException("La asignación no existe");
        }
        return asignacion;
    }

    public List<OpcionAsignacion> maestros() throws SQLException {
        return asignacionDao.maestrosActivos();
    }

    public List<OpcionAsignacion> secciones() throws SQLException {
        return asignacionDao.seccionesConCurriculo();
    }

    public List<CursoSeccion> cursosDeSeccion(int idSeccion) throws SQLException, ServicioException {
        if (idSeccion < 1) {
            throw new DatosInvalidosException("Debes elegir una sección");
        }
        return asignacionDao.cursosDeSeccion(idSeccion);
    }

    public Asignacion crear(PeticionAsignacion datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la asignación");
        }
        datos.validar();
        revisarMaestro(datos.getIdEmpleado());

        int[] curriculo = asignacionDao.datosCurriculoCurso(datos.getIdCurriculoCurso());
        if (curriculo == null) {
            throw new NoEncontradoException("El curso no está en ningún currículo");
        }
        if (curriculo[2] == 0) {
            throw new ConflictoException("Solo se pueden asignar maestros en el año lectivo activo");
        }

        int[] seccion = asignacionDao.datosSeccion(datos.getIdSeccion());
        if (seccion == null) {
            throw new NoEncontradoException("La sección no existe");
        }
        if (seccion[1] == 0) {
            throw new ConflictoException("La sección o su grado están inactivos");
        }
        if (seccion[0] != curriculo[0]) {
            throw new ConflictoException("El curso no pertenece al grado de esa sección");
        }
        if (asignacionDao.existe(datos.getIdCurriculoCurso(), datos.getIdSeccion())) {
            throw new ConflictoException("Ese curso ya tiene un maestro asignado en esa sección");
        }

        int id = asignacionDao.crear(datos);
        return obtener(id);
    }

    public Asignacion cambiarMaestro(int id, PeticionAsignacion datos) throws SQLException, ServicioException {
        Asignacion actual = obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la asignación");
        }
        datos.validarMaestro();
        revisarMaestro(datos.getIdEmpleado());
        if (!asignacionDao.cicloActivo(actual.getIdCiclo())) {
            throw new ConflictoException("Solo se pueden modificar asignaciones del año lectivo activo");
        }
        asignacionDao.cambiarMaestro(id, datos.getIdEmpleado());
        return obtener(id);
    }

    public void eliminar(int id) throws SQLException, ServicioException {
        Asignacion actual = obtener(id);
        if (!asignacionDao.cicloActivo(actual.getIdCiclo())) {
            throw new ConflictoException("Solo se pueden quitar asignaciones del año lectivo activo");
        }
        if (asignacionDao.tieneRegistros(id)) {
            throw new ConflictoException("No se puede quitar: ya tiene ponderaciones o asistencia registradas");
        }
        asignacionDao.eliminar(id);
    }

    private void revisarMaestro(int idEmpleado) throws SQLException, ServicioException {
        String[] empleado = asignacionDao.datosEmpleado(idEmpleado);
        if (empleado == null) {
            throw new NoEncontradoException("El empleado no existe");
        }
        if (!empleado[0].equals("Maestro")) {
            throw new ConflictoException("El empleado no tiene el puesto de Maestro");
        }
        if (!empleado[1].equals("ACTIVO")) {
            throw new ConflictoException("El empleado está inactivo");
        }
    }
}