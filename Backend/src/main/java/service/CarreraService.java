package service;

import dao.CarreraDao;
import dto.PeticionCarrera;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Carrera;

import java.sql.SQLException;
import java.util.List;

public class CarreraService {

    private final CarreraDao carreraDao = new CarreraDao();

    public List<Carrera> listar(int pagina, int tamano) throws SQLException {
        return carreraDao.listar(pagina, tamano);
    }

    public List<Carrera> listarActivas() throws SQLException {
        return carreraDao.listarActivas();
    }

    public int contar() throws SQLException {
        return carreraDao.contar();
    }

    public Carrera obtener(int id) throws SQLException, NoEncontradoException {
        Carrera carrera = carreraDao.buscarPorId(id);
        if (carrera == null) {
            throw new NoEncontradoException("La carrera no existe");
        }
        return carrera;
    }

    public Carrera crear(PeticionCarrera datos) throws SQLException, ServicioException {
        revisar(datos, 0);
        int id = carreraDao.crear(datos);
        return obtener(id);
    }

    public Carrera editar(int id, PeticionCarrera datos) throws SQLException, ServicioException {
        obtener(id);
        revisar(datos, id);
        carreraDao.actualizar(id, datos);
        return obtener(id);
    }

    public Carrera activar(int id) throws SQLException, ServicioException {
        obtener(id);
        carreraDao.cambiarActivo(id, true);
        return obtener(id);
    }

    public Carrera desactivar(int id) throws SQLException, ServicioException {
        obtener(id);
        int grados = carreraDao.contarGradosActivos(id);
        if (grados > 0) {
            throw new ConflictoException("No se puede desactivar: tiene " + grados + " grados activos");
        }
        carreraDao.cambiarActivo(id, false);
        return obtener(id);
    }

    private void revisar(PeticionCarrera datos, int excluirId) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos de la carrera");
        }
        datos.validar();
        if (carreraDao.existeNombre(datos.getNombre(), excluirId)) {
            throw new ConflictoException("Ya existe una carrera con ese nombre");
        }
    }
}