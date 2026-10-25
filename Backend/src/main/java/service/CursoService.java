package service;

import dao.CursoDao;
import dto.PeticionCurso;
import exception.ConflictoException;
import exception.DatosInvalidosException;
import exception.NoEncontradoException;
import exception.ServicioException;
import model.Curso;

import java.sql.SQLException;
import java.util.List;

public class CursoService {

    private final CursoDao cursoDao = new CursoDao();

    public List<Curso> listar(int pagina, int tamano) throws SQLException {
        return cursoDao.listar(pagina, tamano);
    }

    public List<Curso> listarActivos() throws SQLException {
        return cursoDao.listarActivos();
    }

    public int contar() throws SQLException {
        return cursoDao.contar();
    }

    public Curso obtener(int id) throws SQLException, NoEncontradoException {
        Curso curso = cursoDao.buscarPorId(id);
        if (curso == null) {
            throw new NoEncontradoException("El curso no existe");
        }
        return curso;
    }

    public Curso crear(PeticionCurso datos) throws SQLException, ServicioException {
        revisar(datos, 0);
        int id = cursoDao.crear(datos);
        return obtener(id);
    }

    public Curso editar(int id, PeticionCurso datos) throws SQLException, ServicioException {
        obtener(id);
        revisar(datos, id);
        cursoDao.actualizar(id, datos);
        return obtener(id);
    }

    public Curso activar(int id) throws SQLException, ServicioException {
        obtener(id);
        cursoDao.cambiarActivo(id, true);
        return obtener(id);
    }

    public Curso desactivar(int id) throws SQLException, ServicioException {
        obtener(id);
        int usos = cursoDao.contarEnCurriculoActivo(id);
        if (usos > 0) {
            throw new ConflictoException("No se puede desactivar: el curso está en el currículo de un ciclo activo");
        }
        cursoDao.cambiarActivo(id, false);
        return obtener(id);
    }

    private void revisar(PeticionCurso datos, int excluirId) throws SQLException, ServicioException {
        if (datos == null) {
            throw new DatosInvalidosException("Faltan datos del curso");
        }
        datos.validar();
        if (cursoDao.existeNombre(datos.getNombre(), excluirId)) {
            throw new ConflictoException("Ya existe un curso con ese nombre");
        }
    }
}