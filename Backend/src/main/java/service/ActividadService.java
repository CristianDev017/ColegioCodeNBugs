package service;

import dao.ActividadDao;
import dto.PeticionActividad;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Actividad;

import java.sql.SQLException;
import java.util.List;

public class ActividadService {

    private final ActividadDao actividadDao = new ActividadDao();

    public List<Actividad> listar(String buscar, int pagina, int tamano) throws SQLException {
        return actividadDao.listar(buscar, pagina, tamano);
    }

    public int contar(String buscar) throws SQLException {
        return actividadDao.contar(buscar);
    }

    public List<Actividad> listarActivas() throws SQLException {
        return actividadDao.listarActivas();
    }

    public Actividad obtener(int id) throws SQLException, NoEncontradoException {
        Actividad actividad = actividadDao.buscarPorId(id);
        if (actividad == null) {
            throw new NoEncontradoException("La actividad no existe");
        }
        return actividad;
    }

    public Actividad crear(PeticionActividad datos) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la actividad");
        }
        datos.validar();
        if (actividadDao.existeNombre(datos.getNombre(), 0)) {
            throw new ConflictoException("Ya existe una actividad con ese nombre");
        }
        int id = actividadDao.crear(datos);
        return obtener(id);
    }

    public Actividad editar(int id, PeticionActividad datos) throws SQLException, ServicioException {
        Actividad actual = obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la actividad");
        }
        datos.validar();
        if (actividadDao.existeNombre(datos.getNombre(), id)) {
            throw new ConflictoException("Ya existe una actividad con ese nombre");
        }
        if (datos.getCupoTotal() < actual.getInscritos()) {
            throw new ConflictoException("El cupo no puede ser menor a los " + actual.getInscritos()
                    + " estudiantes ya inscritos");
        }
        actividadDao.actualizar(id, datos);
        return obtener(id);
    }

    public Actividad cambiarEstado(int id, boolean activo) throws SQLException, NoEncontradoException {
        obtener(id);
        actividadDao.cambiarEstado(id, activo);
        return obtener(id);
    }
}