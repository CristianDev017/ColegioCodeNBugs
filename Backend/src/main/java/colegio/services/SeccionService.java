
package colegio.services;

import colegio.dao.SeccionDAO;
import colegio.dto.PeticionSeccion;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Seccion;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class SeccionService {

    private final SeccionDAO dao = new SeccionDAO();

    public List<Seccion> listar(int pagina, int tamano)
            throws SQLException {
        return dao.listar(pagina, tamano);
    }

    public int contar() throws SQLException {
        return dao.contar();
    }

    public List<Seccion> listarCatalogo() throws SQLException {
        return dao.listarCatalogo();
    }

    public List<Map<String, Object>> listarGradosDisponibles()
            throws SQLException {
        return dao.listarGradosDisponibles();
    }

    public Seccion buscarPorId(int id)
            throws SQLException, DatosInvalidosException, NoEncontradoException {

        if (id <= 0) {
            throw new DatosInvalidosException("ID de seccion invalido");
        }

        Seccion seccion = dao.buscarPorId(id);

        if (seccion == null) {
            throw new NoEncontradoException("Seccion no encontrada");
        }

        return seccion;
    }

    public void crear(PeticionSeccion datos)
            throws SQLException, DatosInvalidosException, ConflictoException {

        validar(datos, 0);

        try {
            dao.crear(
                    datos.getIdCicloGrado(),
                    datos.getNombre(),
                    datos.getCupo()
            );
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ConflictoException(
                        "La seccion ya existe en este ciclo y grado"
                );
            }
            throw e;
        }
    }

    public void editar(int id, PeticionSeccion datos)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException, ConflictoException {

        Seccion seccion = buscarPorId(id);

        if (seccion.getEstadoCiclo().equals("CERRADO")) {
            throw new ConflictoException(
                    "No puedes modificar secciones de un ciclo cerrado"
            );
        }

        validar(datos, id);

        if (seccion.getIdCicloGrado() != datos.getIdCicloGrado()) {
            throw new ConflictoException(
                    "No puedes cambiar el grado o ciclo de una seccion existente"
            );
        }

        try {
            dao.editar(
                    id,
                    datos.getIdCicloGrado(),
                    datos.getNombre(),
                    datos.getCupo()
            );
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new ConflictoException(
                        "La seccion ya existe en este ciclo y grado"
                );
            }
            throw e;
        }
    }

    public void cambiarEstado(int id, boolean activo)
            throws SQLException, DatosInvalidosException,
            NoEncontradoException, ConflictoException {

        Seccion seccion = buscarPorId(id);

        if (seccion.getEstadoCiclo().equals("CERRADO")) {
            throw new ConflictoException(
                    "No puedes cambiar secciones de un ciclo cerrado"
            );
        }

        dao.cambiarEstado(id, activo);
    }

    private void validar(PeticionSeccion datos, int idExcluir)
            throws SQLException, DatosInvalidosException, ConflictoException {

        if (datos == null) {
            throw new DatosInvalidosException(
                    "Debes enviar los datos de la seccion"
            );
        }

        datos.validar();

        String estado = dao.obtenerEstadoCicloGrado(
                datos.getIdCicloGrado()
        );

        if (estado == null) {
            throw new DatosInvalidosException(
                    "El grado no existe o no esta habilitado para ese ciclo"
            );
        }

        if (estado.equals("CERRADO")) {
            throw new ConflictoException(
                    "No puedes administrar secciones de un ciclo cerrado"
            );
        }

        if (dao.existeDuplicado(
                datos.getIdCicloGrado(),
                datos.getNombre(),
                idExcluir
        )) {
            throw new ConflictoException(
                    "Ya existe esa seccion en el mismo ciclo y grado"
            );
        }
    }
}
