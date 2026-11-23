package colegio.services;

import colegio.dao.CurriculoDAO;
import colegio.dto.PeticionCurriculo;
import colegio.dto.PeticionCursoCurriculo;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Curriculo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CurriculoService {

    private final CurriculoDAO dao = new CurriculoDAO();

    public List<Curriculo> listar() throws SQLException {
        return dao.listar();
    }

    public List<Map<String, Object>> listarCursos(int idCurriculo)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException {

        validarCurriculo(idCurriculo);

        return dao.listarCursos(idCurriculo);
    }

    public void crear(PeticionCurriculo datos)
            throws SQLException, DatosInvalidosException,
            ConflictoException {

        if (datos == null) {
            throw new DatosInvalidosException("Ingresa los datos del curriculo");
        }

        datos.validar();

        if (!dao.existeGrado(datos.getIdGrado())) {
            throw new DatosInvalidosException("El grado no existe");
        }

        if (!dao.existeCiclo(datos.getIdCiclo())) {
            throw new DatosInvalidosException("El ciclo escolar no existe");
        }

        if (dao.existeCombinacion(
                datos.getIdGrado(), datos.getIdCiclo())) {

            throw new ConflictoException(
                    "Ya existe un curriculo para este grado y ciclo"
            );
        }

        try {
            dao.crear(datos.getIdGrado(), datos.getIdCiclo());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ConflictoException(
                        "Ya existe un curriculo para este grado y ciclo"
                );
            }
            throw e;
        }
    }

    public void agregarCurso(int idCurriculo, PeticionCursoCurriculo datos)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException, ConflictoException {

        validarCurriculo(idCurriculo);

        if (datos == null) {
            throw new DatosInvalidosException("Selecciona un curso");
        }

        datos.validar();

        if (!dao.existeCurso(datos.getIdCurso())) {
            throw new DatosInvalidosException("El curso no existe");
        }

        if (dao.existeCursoEnCurriculo(
                idCurriculo, datos.getIdCurso())) {

            throw new ConflictoException(
                    "Este curso ya pertenece al curriculo"
            );
        }

        try {
            dao.agregarCurso(idCurriculo, datos.getIdCurso());
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ConflictoException(
                        "Este curso ya pertenece al curriculo"
                );
            }
            throw e;
        }
    }

    public void quitarCurso(int idCurriculo, int idCurso)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException {

        validarCurriculo(idCurriculo);

        if (idCurso <= 0) {
            throw new DatosInvalidosException("ID de curso invalido");
        }

        if (!dao.quitarCurso(idCurriculo, idCurso)) {
            throw new NoEncontradoException(
                    "El curso no esta asignado a este curriculo"
            );
        }
    }

    private void validarCurriculo(int idCurriculo)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException {

        if (idCurriculo <= 0) {
            throw new DatosInvalidosException("ID de curriculo invalido");
        }

        if (!dao.existeCurriculo(idCurriculo)) {
            throw new NoEncontradoException("Curriculo no encontrado");
        }
    }
}
