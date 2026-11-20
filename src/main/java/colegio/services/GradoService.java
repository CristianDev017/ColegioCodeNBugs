package colegio.services;

import colegio.dao.GradoDAO;
import colegio.dto.PeticionGrado;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Grado;

import java.sql.SQLException;
import java.util.List;

public class GradoService {

    private GradoDAO dao = new GradoDAO();

    public List<Grado> listar() throws SQLException {
        return dao.listar();
    }

    public Grado buscarPorId(int id)
            throws SQLException, NoEncontradoException {

        Grado grado = dao.buscarPorId(id);

        if (grado == null) {
            throw new NoEncontradoException("No se encontro el grado");
        }

        return grado;
    }

    public void crear(PeticionGrado datos)
            throws SQLException, DatosInvalidosException, ConflictoException {

        validarDatos(datos, 0);

        dao.crear(
                datos.getIdNivel(),
                datos.getIdCarrera(),
                datos.getNombre(),
                datos.getCupoDefault()
        );
    }

    public void editar(int id, PeticionGrado datos)
            throws SQLException, DatosInvalidosException,
            ConflictoException, NoEncontradoException {

        buscarPorId(id);
        validarDatos(datos, id);

        dao.editar(
                id,
                datos.getIdNivel(),
                datos.getIdCarrera(),
                datos.getNombre(),
                datos.getCupoDefault()
        );
    }

    public void cambiarEstado(int id, boolean activo)
            throws SQLException, NoEncontradoException {

        buscarPorId(id);
        dao.cambiarEstado(id, activo);
    }

    private void validarDatos(PeticionGrado datos, int idExcluir)
            throws SQLException, DatosInvalidosException, ConflictoException {

        if (datos == null) {
            throw new DatosInvalidosException("Debes enviar los datos del grado");
        }

        datos.validar();

        String nivel = dao.buscarNombreNivel(datos.getIdNivel());

        if (nivel == null) {
            throw new DatosInvalidosException("El nivel no existe");
        }

        boolean diversificado = nivel.equalsIgnoreCase("Diversificado");

        if (diversificado && datos.getIdCarrera() == null) {
            throw new DatosInvalidosException("Selecciona una carrera para Diversificado");
        }

        if (!diversificado && datos.getIdCarrera() != null) {
            throw new DatosInvalidosException("Este nivel no necesita carrera");
        }

        if (datos.getIdCarrera() != null &&
                !dao.existeCarreraActiva(datos.getIdCarrera())) {
            throw new DatosInvalidosException("La carrera no existe o esta inactiva");
        }

        if (dao.existeDuplicado(
                datos.getIdNivel(),
                datos.getIdCarrera(),
                datos.getNombre(),
                idExcluir)) {

            throw new ConflictoException("Ya existe ese grado en el nivel seleccionado");
        }
    }
}
