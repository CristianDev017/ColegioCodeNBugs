package service;

import dao.EstudianteDao;
import dto.PeticionEstado;
import dto.PeticionEstudiante;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Estudiante;
import util.PasswordService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class EstudianteService {

    private final EstudianteDao estudianteDao = new EstudianteDao();

    public List<Estudiante> listar(String buscar, String estado, int pagina, int tamano) throws SQLException {
        return estudianteDao.listar(buscar, estado, pagina, tamano);
    }

    public int contar(String buscar, String estado) throws SQLException {
        return estudianteDao.contar(buscar, estado);
    }

    public Estudiante obtener(int id) throws SQLException, NoEncontradoException {
        Estudiante estudiante = estudianteDao.buscarPorId(id);
        if (estudiante == null) {
            throw new NoEncontradoException("El estudiante no existe");
        }
        return estudiante;
    }

    public Estudiante crear(PeticionEstudiante datos) throws SQLException, ServicioException {
        revisar(datos, 0, 0);
        String carnet = estudianteDao.siguienteCarnet(LocalDate.now().getYear());
        int id = estudianteDao.crear(datos, carnet, PasswordService.encriptar(carnet));
        return obtener(id);
    }

    public Estudiante editar(int id, PeticionEstudiante datos) throws SQLException, ServicioException {
        Estudiante actual = obtener(id);
        int idEncargado = actual.getEncargado() == null ? 0 : actual.getEncargado().getId();
        if (idEncargado == 0) {
            throw new ConflictoException("El estudiante no tiene encargado registrado");
        }
        revisar(datos, id, idEncargado);
        estudianteDao.actualizar(id, idEncargado, datos);
        return obtener(id);
    }

    public Estudiante cambiarEstado(int id, PeticionEstado datos) throws SQLException, ServicioException {
        Estudiante actual = obtener(id);
        if (datos == null) {
            throw new DatosInvalidosException("Falta el estado");
        }
        datos.validar();
        if (actual.getEstado().equals(datos.getEstado())) {
            throw new ConflictoException("El estudiante ya está en estado " + datos.getEstado());
        }
        estudianteDao.cambiarEstado(id, datos.getEstado());
        return obtener(id);
    }

    private void revisar(PeticionEstudiante datos, int excluirEstudiante, int excluirEncargado) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del estudiante");
        }
        datos.validar();
        if (datos.getDpi() != null && estudianteDao.existeDpi(datos.getDpi(), excluirEstudiante)) {
            throw new ConflictoException("Ya existe un estudiante con ese DPI");
        }
        if (excluirEstudiante != 0 && estudianteDao.existeDpiEncargado(datos.getEncargado().getDpi(), excluirEncargado)) {
            throw new ConflictoException("Ese DPI de encargado ya pertenece a otra persona");
        }
    }
}