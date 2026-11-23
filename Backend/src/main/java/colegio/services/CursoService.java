package colegio.services;

import colegio.dao.CursoDAO;
import colegio.dto.PeticionCurso;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Curso;

import java.sql.SQLException;
import java.util.List;

public class CursoService {

    private CursoDAO dao = new CursoDAO();

    public List<Curso> listar() throws SQLException {
        return dao.listar();
    }

    public Curso buscarPorId(int id)
            throws SQLException, DatosInvalidosException, NoEncontradoException {

        if (id <= 0) {
            throw new DatosInvalidosException("ID de curso invalido");
        }

        Curso curso = dao.buscarPorId(id);

        if (curso == null) {
            throw new NoEncontradoException("No se encontro el curso");
        }

        return curso;
    }

    public void crear(PeticionCurso datos)
            throws SQLException, DatosInvalidosException, ConflictoException {

        if (datos == null) {
            throw new DatosInvalidosException("Debes enviar los datos del curso");
        }

        datos.validar();

        if (dao.nombreOcupado(datos.getNombre(), 0)) {
            throw new ConflictoException("Ya existe un curso con ese nombre");
        }

        try {
            dao.crear(datos.getNombre(), datos.getDescripcion());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ConflictoException("Ya existe un curso con ese nombre");
            }
            throw e;
        }
    }

    public void editar(int id, PeticionCurso datos)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException, ConflictoException {

        buscarPorId(id);

        if (datos == null) {
            throw new DatosInvalidosException("Debes enviar los datos del curso");
        }

        datos.validar();

        if (dao.nombreOcupado(datos.getNombre(), id)) {
            throw new ConflictoException("Otro curso ya tiene ese nombre");
        }

        try {
            dao.editar(id, datos.getNombre(), datos.getDescripcion());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ConflictoException("Otro curso ya tiene ese nombre");
            }
            throw e;
        }
    }

    public void cambiarEstado(int id, boolean activo)
            throws SQLException, DatosInvalidosException, NoEncontradoException {

        buscarPorId(id);
        dao.cambiarEstado(id, activo);
    }
}
