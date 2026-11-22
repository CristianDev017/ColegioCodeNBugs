package colegio.services;

import colegio.dao.CarreraDAO;
import colegio.dto.PeticionCarrera;
import colegio.exception.ConflictoException;
import colegio.exception.DatosInvalidosException;
import colegio.exception.NoEncontradoException;
import colegio.model.Carrera;

import java.sql.SQLException;
import java.util.List;

public class CarreraService {

    private final CarreraDAO dao = new CarreraDAO();

    public List<Carrera> listar() throws SQLException {
        return dao.listar();
    }

    public Carrera buscarPorId(int idCarrera)
            throws SQLException, DatosInvalidosException, NoEncontradoException {

        if (idCarrera <= 0) {
            throw new DatosInvalidosException("El id de la carrera no es valido");
        }

        Carrera carrera = dao.buscarPorId(idCarrera);

        if (carrera == null) {
            throw new NoEncontradoException("Carrera no encontrada");
        }

        return carrera;
    }

    public void crear(PeticionCarrera datos)
            throws SQLException, DatosInvalidosException, ConflictoException {

        datos.validar();

        if (dao.existeNombre(datos.getNombre())) {
            throw new ConflictoException("Ya existe una carrera con ese nombre");
        }

        Carrera carrera = new Carrera(
                0,
                datos.getNombre(),
                datos.getDescripcion(),
                true
        );

        dao.crear(carrera);
    }

    public void editar(int idCarrera, PeticionCarrera datos)
            throws SQLException, DatosInvalidosException, NoEncontradoException, ConflictoException {

        if (idCarrera <= 0) {
            throw new DatosInvalidosException("El id de la carrera no es valido");
        }

        datos.validar();

        // Comprobamos que el nombre no este registrado en otra carrera
        if (dao.existeNombreEnOtraCarrera(datos.getNombre(), idCarrera)) {
            throw new ConflictoException("Ya existe una carrera con ese nombre");
        }

        Carrera carrera = new Carrera(
                idCarrera,
                datos.getNombre(),
                datos.getDescripcion(),
                true
        );

        if (!dao.editar(carrera)) {
            throw new NoEncontradoException("Carrera no encontrada");
        }
    }

    public void cambiarEstado(int idCarrera, boolean activo)
            throws SQLException, DatosInvalidosException, NoEncontradoException {

        if (idCarrera <= 0) {
            throw new DatosInvalidosException("El id de la carrera no es valido");
        }

        if (!dao.cambiarEstado(idCarrera, activo)) {
            throw new NoEncontradoException("Carrera no encontrada");
        }
    }
}
